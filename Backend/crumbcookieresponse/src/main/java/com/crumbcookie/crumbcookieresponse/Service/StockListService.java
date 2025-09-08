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


  /* @Autowired
  private StockPriceEntity existStockPriceEntity; */

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
    if (exDTORedisOptional.isPresent()) {
      exDTO = exDTORedis;
    } else {
      List<YahooStockOpenDTO> exDTOFromMarket = this.yahooExAPIService.getExMarket();
      exDTO = exDTOFromMarket.stream().map(e -> this.entityMapper.mapExQuickStore(e.getChart().getResult().get(0))).toList();
    }

    /* StocksEntity stocksEntity = new StocksEntity();
    StockPriceEntity stockPriceEntity = new StockPriceEntity();
    StockPriceEntity addstockPriceEntity = new StockPriceEntity(); */
    

   //open redis
   Map<String, Double> redisData = this.redisManager.get("stockList", new TypeReference<Map<String, Double>>() {});
   //Map<String, Double> redisData = new HashMap<>();
   /* try {
     redisData = this.redisManager.get(
        "stockList", 
        new TypeReference<Map<String, Double>>() {}
    );
} catch (MismatchedInputException e) {
    //redisManager..delete("stockList");
    redisManager.set("stockList", new HashMap<>());
} */
   Map<String, Double> quickGetPrice = new HashMap<>();
    


   /* if (redisData != null) {
    return dto;
   } */

 //   YahooStockDTO dtoRedisData = this.redisManager.get("0388.HK", YahooStockDTO.class);
    //List<String> stocklistInRedis = new ArrayList<>();
    /* this.stockPriceRepository.deleteAll();
    this.stocksRepository.deleteAll(); */

    //StocksEntity stocksEntity = new  StocksEntity();
    //StockPriceEntity stockPriceEntity = new StockPriceEntity();
    //List<StockPriceEntity> stockPriceEntityList = this.stockPriceRepository.findAll() != null ? this.stockPriceRepository.findAll() : new  ArrayList<>();


    //for (YahooStockDTO.QuoteBody.QuoteResult result : dto.getBody().getResults()) {
    for (YahooStockDTO result : dto) {
        for (YahooStockDTO.QuoteBody.QuoteResult ouput : result.getBody().getResults()) {
          if (existRedisOptional.isPresent()) {
            if (existRedis.stream().anyMatch(redis -> redis.getRegularMarketTime().equals(MarketTimeManager.longToLocalDateTime(ouput.getRegularMarketTime()))
              &&  redis.getSymbol().equals(ouput.getSymbol()))) {
              continue;
            } 
             
            
           // StocksEntity stocksEntity = this.entityMapper.mapStockName(ouput); 
          
           // StockPriceEntity stockPriceEntity = this.entityMapper.mapStockPrice(ouput); 

            //stockPriceEntity.setStocksEntity(stocksEntity);
            //this.stocksRepository.save(stocksEntity);
           // this.stockPriceRepository.save(stockPriceEntity);
            //System.out.println("save update price to DB");

          }
          
            Optional<StocksEntity> stockentityOpt = this.stocksRepository.findBySymbol(ouput.getSymbol()) ;
            StocksEntity stocksEntity;
            if (stockentityOpt.isPresent()) {
              //StocksEntity existstocksEntity = stockentityOpt.get();
              stocksEntity = stockentityOpt.get();
              //StockPriceEntity updatePriceEntity = this.entityMapper.mapStockPrice(ouput); 
              //updatePriceEntity.setStocksEntity(existstocksEntity);
              //this.stockPriceRepository.save(updatePriceEntity);
              //System.out.println("save update price to DB");

            } else{
            stocksEntity = this.entityMapper.mapStockName(ouput); 
            this.stocksRepository.save(stocksEntity);
            

            //stocksEntity = this.stocksRepository.save(stocksEntity);
            


            //if (!existRedis.stream().anyMatch(redis -> redis.getSymbol().equals(stocksEntity.getSymbol()))) {
            //  this.stocksRepository.save(stocksEntity);
            //}
            

            }
            //int hour = this.entityMapper.longToLocalTime(ouput.getRegularMarketTime()).getHour();
            //int min = this.entityMapper.longToLocalTime(ouput.getRegularMarketTime()).getHour();
            //ExQuickStore openPoinStore = exDTO.stream().filter(e -> e.getSymbol().equals(ouput.getSymbol()) && e.getTimestamp().getHour() == hour &&
            //  e.getTimestamp().getMinute() == min).findFirst().get();
            StockPriceEntity stockPriceEntity = this.entityMapper.mapStockPrice(ouput, exDTO); 
            stockPriceEntity.setStocksEntity(stocksEntity);
            this.stockPriceRepository.save(stockPriceEntity);
            System.out.println("save update price to DB");


      String stockName = ouput.getSymbol();
      Double stockPrice = ouput.getRegularMarketPrice();
      Double stockOpen = stockPriceEntity.getOpen();
      quickGetPrice.put(stockName, stockPrice);

      marketTime = "" + this.entityMapper.longToLocalTime(ouput.getRegularMarketTime()) ;

    //RedisQuickStore dtoRedisBuilder  = RedisQuickStore.builder().symbol(stockName)
    //  .symbolFullName(ouput.getLongName())
    //  .regularMarketTime(MarketTimeManager.longToLocalDateTime(ouput.getRegularMarketTime()))
    //  .regularMarketPrice(stockPrice).regularMarketChangePercent(ouput.getRegularMarketChangePercent())
    //  .regularMarketOpen(ouput.getRegularMarketOpen())
    //  .regularMarketDayHigh(ouput.getRegularMarketDayHigh())
    //  .regularMarketDayLow(ouput.getRegularMarketDayLow())
    //  .bid(ouput.getBid()).ask(ouput.getAsk()).build();
    RedisQuickStore dtoRedisBuilder  = this.entityMapper.mapQuickStore(ouput, stockOpen);

      redisDtoResult.add(dtoRedisBuilder);

        }
        
    }

   /*  for (RedisQuickStore DtoResult : redisDtoResult) {
      addstockPriceEntity = this.entityMapper.mapStockPrice(DtoResult);
      addstockPriceEntity.setStocksEntity(stocksEntity);
      this.stockPriceRepository.save(addstockPriceEntity);
      
    }  */
    
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
    /* try {
     this.redisManager.set("stockList",quickGetPrice ,Duration.ofMinutes(6) 
    );
} catch (MismatchedInputException e) {
    //redisManager..delete("stockList");
    this.redisManager.set("stockList",quickGetPrice ,Duration.ofMinutes(6)); 
} */

   
    this.redisManager.set("CallDataTime", marketTime, Duration.ofMinutes(6));
    System.out.println(LocalDateTime.now());
    return dto;
    
  }

 


}
