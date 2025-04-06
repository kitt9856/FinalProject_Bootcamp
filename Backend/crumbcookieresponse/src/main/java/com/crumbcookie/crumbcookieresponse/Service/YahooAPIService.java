package com.crumbcookie.crumbcookieresponse.Service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.crumbcookie.crumbcookieresponse.Appcofig.AppConfig;
import com.crumbcookie.crumbcookieresponse.Appcofig.RedisRestartManager;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.Entity.mapper.EntityMapper;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class YahooAPIService {


  @Autowired
  private CrumbManager crumbManager;

  @Autowired
  private StocksRepository stocksRepository;
  
  @Autowired
  private StockPriceRepository stockPriceRepository;

  @Autowired
  private EntityMapper entityMapper;


  @Autowired
  private RedisManager redisManager;
    

  public List<String> getSymbols() throws Exception {
    List<String> symbolRedis = this.redisManager.get("stockSybmol",
     new TypeReference<List<String>>() {});
    if (symbolRedis != null) {
      return symbolRedis;
    } else {
      List<String> symbolDB = new ArrayList<>();
      this.stocksRepository.findAll().forEach(e -> symbolDB.add(e.getSymbol()));
      this.redisManager.set("stockSymbol", symbolDB);
      return symbolDB;
    }
  }

  public Map<String, Double> getPriceofStock() throws Exception {
    Map<String, Double> stockPriceList = this.redisManager.get("stockList", 
      new TypeReference<Map<String, Double>>() {});
    if (stockPriceList != null) {
      return stockPriceList;
    } else {
      Map<String, Double> priceListDB = new HashMap<>();
      this.stockPriceRepository.findAll().forEach(e -> priceListDB.put(
        e.getStocksEntity().getSymbol(), e.getMarketPrice()));
      this.redisManager.set("stockList", priceListDB, Duration.ofMinutes(6));
      return priceListDB;
    }
  }
  
  public YahooStockDTO getStockDTO(List<String> symbols) throws Exception {
    
    crumbManager.getKey();
    String crumb = crumbManager.getStrFromgGetCrum();
    String cookie = crumbManager.getYahooCookie();
   

          MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
      String symbolParam = String.join(",", symbols);
      httpParams.put("symbols", List.of(symbolParam) );
      httpParams.put("crumb", List.of(crumb));

      String url = UriComponentsBuilder.newInstance().scheme("https")
        .host("query1.finance.yahoo.com").path("/v7").path("/finance").path("/quote")
        .queryParams(httpParams)
        .toUriString();
      System.out.println(url);

      HttpHeaders headers = new HttpHeaders();
      headers.set("Cookie", cookie);
      headers.set("User-Agent", "Mozilla/5.0");
      headers.set("Crumb", crumb);

      System.out.println(headers);
      HttpEntity<String> entity = new HttpEntity<>(headers);

      RestTemplate restTemplate = new RestTemplate();
      restTemplate = new RestTemplateBuilder() //
        .connectTimeout(Duration.ofSeconds(20)) //
        .readTimeout(Duration.ofSeconds(20)) //
        .build();
      ResponseEntity<YahooStockDTO> response ;

      do {
        response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockDTO.class);
        if (response.getBody().getBody().getResults() == null && 
          response.getBody().getBody().getError().getCode().equals("Unauthorized")) {
            while (response.getBody().getBody().getError().getCode().equals("Unauthorized")) {
              TimeUnit.SECONDS.sleep(10);
              getStockDTO(symbols);
            }
        }
      } while (!response.getStatusCode().is2xxSuccessful());

      return response.getBody();
  }


    public List<RedisQuickStore>  getData()throws JsonProcessingException {
      Boolean checkBreakTime = MarketTimeManager.isBreakTime(LocalTime.now());
      Boolean checktradeDay = MarketTimeManager.validTradeDay(LocalDate.now());
      List<RedisQuickStore> result = this.redisManager.get("DTOResult", new TypeReference<List<RedisQuickStore>>() {});
      List<RedisQuickStore> dbStores = new ArrayList<>();
      
      //move to Scheduled task
      /*
      LocalTime prevMarketTime = LocalTime.of(8, 30);
      LocalTime openMarketTime = LocalTime.of(9, 30); */
      /* if (checkTimer.isAfter(prevMarketTime) && checkTimer.isBefore(openMarketTime) 
          && (checktradeDay) ) {
        this.stockPriceRepository.deleteAll();
        this.redisManager.deleteAll();
        //return empty list of redis
        return result;
      } else { */


        if (result != null  || (!checkBreakTime && checktradeDay) || !checktradeDay) {
          //redis not null return reid first
         return result;
        } else 
          if (!checkBreakTime && checktradeDay) {
            return Collections.emptyList();
         }
         if (result == null || result.isEmpty()) {
           //otherwise use DB data
        
        for (StockPriceEntity entity : this.stockPriceRepository.findAll()) {
          RedisQuickStore dStore = RedisQuickStore.builder().symbol(entity.getStocksEntity().getSymbol())
            .regularMarketPrice(entity.getMarketPrice())
            .regularMarketChangePercent(entity.getRegularMarketChangePercent())
            .regularMarketOpen(entity.getOpen())
            .regularMarketDayHigh(entity.getHigh())
            .regularMarketDayLow(entity.getLow())
            .bid(entity.getBid()).ask(entity.getAsk()).regularMarketTime((LocalDateTime.of(entity.getTradeDate(), entity.getPriceUpdatetime()) ))
            .build();
          dbStores.add(dStore);
        }
        this.redisManager.set("DTOResult", dbStores, Duration.ofDays(2));
          //return dbStores.isEmpty() ? Collections.emptyList() : dbStores;
        
      }
      
      return dbStores;
        
       
      
    }


  public static void main(String[] args) {
    LocalTime time = LocalTime.now();
    System.out.println(time);
  }
}
