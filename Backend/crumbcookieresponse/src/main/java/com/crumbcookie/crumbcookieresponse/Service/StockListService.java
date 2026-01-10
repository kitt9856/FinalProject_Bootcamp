package com.crumbcookie.crumbcookieresponse.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.Entity.mapper.EntityMapper;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.dto.ExQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockOpenDTO;
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.crumbcookie.crumbcookieresponse.model.StockStore;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class StockListService {
  
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private YahooAPIService yahooAPIService;

  @Autowired
  private StocksRepository stocksRepository;
  
  @Autowired
  private StockPriceRepository stockPriceRepository;

  /* @Autowired
  private StocksEntity stocksEntity; */

  @Autowired
  private EntityMapper entityMapper;

  @Autowired
  private RedisManager redisManager;

  @Autowired
  private StockStore stockStore;

  @Autowired
  private MarketTimeManager marketTimeManager;

  @Autowired
  private YahooExAPIService yahooExAPIService;


  

  //get market data
  public List<YahooStockDTO> getMarket() throws Exception{
    List<String>  symbols =  stockStore.getTargetSybmols() ;
    System.out.println(symbols);
    String marketTime = "";
    String findsymbolOpen = "";

    List<YahooStockDTO> dto = new ArrayList<>();
    //List<YahooStockDTO> dto = (yahooAPIService.getStockDTO(symbols));
    if (!this.yahooAPIService.getStockUpdateDTO(symbols).isEmpty()) {
       dto = yahooAPIService.getStockUpdateDTO(symbols);
    } else{
      dto = (yahooAPIService.getStockDTO(symbols));
    }
    
    //YahooStockDTO dto = yahooAPIService.getStockDTO(symbols);
    //List<YahooStockDTO> dTOs = Arrays.asList(yahooAPIService.getStockDTO(symbols));
    List<RedisQuickStore> redisDtoResult = new ArrayList<>();
      List<RedisQuickStore> existRedis = this.redisManager.get("DTOResult", new TypeReference<List<RedisQuickStore>>() {});
    Optional<List<RedisQuickStore>> existRedisOptional = Optional.ofNullable(existRedis);

    List<ExQuickStore> exDTO = new ArrayList<>();
    List<ExQuickStore> exDTORedis = this.redisManager.get("ExDTOResult", new TypeReference<List<ExQuickStore>>() {});
    Optional<List<ExQuickStore>> exDTORedisOptional = Optional.ofNullable(exDTORedis);
    LocalDateTime exDTORedisUpdTime = this.redisManager.get("ExDTOlastUpdate", new TypeReference<LocalDateTime>() {});
    if (exDTORedisOptional.isPresent()) {
      LocalDateTime checkupdTime = LocalDateTime.now();
      //如redis的資料vs NowTime相差少於25秒，則使用redis資料
      if (exDTORedisUpdTime != null && (Duration.between(exDTORedisUpdTime, checkupdTime).getSeconds() < 25
      || exDTORedisUpdTime.isEqual(checkupdTime))) { 
        exDTO = exDTORedis;
      } else {
        List<YahooStockOpenDTO> exDTOFromMarket = this.yahooExAPIService.getExMarket();
        exDTO = exDTOFromMarket.stream().map(e -> this.entityMapper.mapExQuickStore(e.getChart().getResult().get(0))).toList();
      }
    } else {
      List<YahooStockOpenDTO> exDTOFromMarket = this.yahooExAPIService.getExMarket();
      exDTO = exDTOFromMarket.stream().map(e -> this.entityMapper.mapExQuickStore(e.getChart().getResult().get(0))).toList();
    }

   //open redis
   Map<String, Double> redisData = this.redisManager.get("stockList", new TypeReference<Map<String, Double>>() {});

   Map<String, Double> quickGetPrice = new HashMap<>();

    //for (YahooStockDTO.QuoteBody.QuoteResult result : dto.getBody().getResults()) {
    //loop 實時 dto   
    for (YahooStockDTO result : dto) {
        for (YahooStockDTO.QuoteBody.QuoteResult ouput : result.getBody().getResults()) {
          if (existRedisOptional.isPresent()) {
            //check if data exist in redis, if exist skip 
            if (existRedis.stream().anyMatch(redis -> redis.getRegularMarketTime().equals(MarketTimeManager.longToLocalDateTime(ouput.getRegularMarketTime()))
              &&  redis.getSymbol().equals(ouput.getSymbol()))) {
              continue;
            } 
          

          }
            //在DB中找目前symbol
            Optional<StocksEntity> stockentityOpt = this.stocksRepository.findBySymbol(ouput.getSymbol()) ;
            StocksEntity stocksEntity;
            if (stockentityOpt.isPresent()) {
              //StocksEntity existstocksEntity = stockentityOpt.get();
              stocksEntity = stockentityOpt.get();
              //System.out.println("save update price to DB");

            } else{
            stocksEntity = this.entityMapper.mapStockName(ouput); 
            this.stocksRepository.save(stocksEntity);
            

            }
            //處理exDTO資料，然後補充low,high,open  
            StockPriceEntity stockPriceEntity = this.entityMapper.mapStockPrice(ouput, exDTO); 
            stockPriceEntity.setStocksEntity(stocksEntity);
            this.stockPriceRepository.save(stockPriceEntity);
            System.out.println("save update price to DB");

      //return these data  
      String stockName = ouput.getSymbol();
      Double stockPrice = ouput.getRegularMarketPrice();
      Double stockOpen = stockPriceEntity.getOpen();
      Double stockHigh = stockPriceEntity.getHigh();
      Double stockLow = stockPriceEntity.getLow();

      quickGetPrice.put(stockName, stockPrice);

      marketTime = "" + this.entityMapper.longToLocalTime(ouput.getRegularMarketTime()) ;

    RedisQuickStore dtoRedisBuilder  = this.entityMapper.mapQuickStore(ouput, stockOpen,stockHigh, stockLow);

      redisDtoResult.add(dtoRedisBuilder);

        }
        
    }

   
    
    if (existRedisOptional.isPresent()) {
      redisDtoResult.addAll(existRedis);
    }
    System.out.println("DTO data set to redis");

    //store symbol,regularMarketTime,regularMarketPrice,regularMarketChangePercent,bid
    this.redisManager.set("DTOResult", redisDtoResult, Duration.ofDays(2));

     //store stock name only
     this.redisManager.set("stockSybmol", symbols);

    //redis store Map of "stockname" : stockprice;...
    this.redisManager.set("stockList",quickGetPrice ,Duration.ofMinutes(6) );

   
    this.redisManager.set("CallDataTime", marketTime, Duration.ofMinutes(6));
    System.out.println(LocalDateTime.now());
    return dto;
    
  }

 


}
