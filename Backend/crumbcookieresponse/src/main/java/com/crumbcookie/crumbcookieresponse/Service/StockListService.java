package com.crumbcookie.crumbcookieresponse.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.Entity.mapper.EntityMapper;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO.QuoteBody.QuoteResult;
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  @Autowired
  private EntityMapper entityMapper;

  @Autowired
  private RedisManager redisManager;

  public YahooStockDTO getMarket() throws Exception{
    List<String>  symbols =  List.of("0388.HK", "0700.HK");
    String marketTime = "";

    YahooStockDTO dto = yahooAPIService.getStockDTO(symbols);
    List<RedisQuickStore> redisDtoResult = new ArrayList<>();

   //open redis
   Map<String, Double> redisData = this.redisManager.get("stockList", new TypeReference<Map<String, Double>>() {});
   Map<String, Double> quickGetPrice = new HashMap<>();
   if (redisData != null) {
    return dto;
   }

 //   YahooStockDTO dtoRedisData = this.redisManager.get("0388.HK", YahooStockDTO.class);
    //List<String> stocklistInRedis = new ArrayList<>();
    /* this.stockPriceRepository.deleteAll();
    this.stocksRepository.deleteAll(); */


    for (YahooStockDTO.QuoteBody.QuoteResult result : dto.getBody().getResults()) {
     StocksEntity stocksEntity = this.entityMapper.mapStockName(result);
      StockPriceEntity stockPriceEntity = this.entityMapper.mapStockPrice(result);
      stockPriceEntity.setStocksEntity(stocksEntity);
      

      this.stocksRepository.save(stocksEntity);
      this.stockPriceRepository.save(stockPriceEntity);

      String stockName = result.getSymbol();
      Double stockPrice = result.getRegularMarketPrice();
      quickGetPrice.put(stockName, stockPrice);

      marketTime = "" + this.entityMapper.longToLocalTime(result.getRegularMarketTime()) ;

    RedisQuickStore dtoRedisBuilder  = RedisQuickStore.builder().symbol(stockName)
      .regularMarketTime(MarketTimeManager.longToLocalDateTime(result.getRegularMarketTime()))
      .regularMarketPrice(stockPrice).regularMarketChangePercent(result.getRegularMarketChangePercent())
      .regularMarketOpen(result.getRegularMarketOpen())
      .regularMarketDayHigh(result.getRegularMarketDayHigh())
      .regularMarketDayLow(result.getRegularMarketDayLow())
      .bid(result.getBid()).ask(result.getAsk()).build();

      redisDtoResult.add(dtoRedisBuilder);
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
