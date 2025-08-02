package com.crumbcookie.crumbcookieresponse.Service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
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
import com.crumbcookie.crumbcookieresponse.Controller.DataAPIController;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceOHEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceOHRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockApiResponse;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.DateTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//import jakarta.annotation.PostConstruct;

@Service
public class YahooAPIService {


  @Autowired
  private CrumbManager crumbManager;

  @Autowired
  private StocksRepository stocksRepository;
  
  @Autowired
  private StockPriceRepository stockPriceRepository;

  @Autowired
  private StockPriceOHRepository stockPriceOHRepository;

  /* @Autowired
  private EntityMapper entityMapper; */

  @Autowired
  private DateTimeManager dateTimeManager;

  @Autowired
  private RestTemplate restTemplate;


  @Autowired
  private RedisManager redisManager;

  @Autowired
  private DataAPIController dataAPIController;


  public List<String> getSymbols() throws Exception {
    List<String> symbolRedis = this.redisManager.get("stockSybmol",
     new TypeReference<List<String>>() {});
    Optional<List<String>> symbolRedisOpt = Optional.ofNullable(symbolRedis);
    //if (symbolRedis != null) {
    if (symbolRedisOpt.isPresent()) {
      return symbolRedis;
    } else {
      List<String> symbolDB = new ArrayList<>();
      this.stocksRepository.findAll().forEach(e -> symbolDB.add(e.getSymbol()));
      this.redisManager.set("stockSymbol", symbolDB);
      return symbolDB;
    }
  }

  public Map<String,String> getSymbolsName() throws Exception {
    Map<String,String> symbolRedis = this.redisManager.get("stockSybmolName",
     new TypeReference<Map<String,String>>() {});
    Optional<Map<String,String>> symbolRedisOpt = Optional.ofNullable(symbolRedis);
    //if (symbolRedis != null) {
    if (symbolRedisOpt.isPresent()) {
      return symbolRedis;
    } else {
      //Map<String,String> symbolDB = new ArrayList<>();
      Map<String,String> symbolmap = new HashMap<>();
      this.stocksRepository.findAll().forEach(e ->{
        String symbol = e.getSymbol();
        symbolmap.put(symbol, e.getLongName());
        //symbolmap.put("name", e.getLongName());
       // symbolDB.add(symbolmap);
      } );
      this.redisManager.set("stockSybmolName", symbolmap);
      return symbolmap;
    }
  }

  public Map<String, Double> getPriceofStock() throws Exception {
    Map<String, Double> stockPriceList = this.redisManager.get("stockList", 
      new TypeReference<Map<String, Double>>() {});
    if (stockPriceList != null) {
      return stockPriceList;
    } else {
      Map<String, Double> priceListDB = new HashMap<>();
     /*  this.stockPriceRepository.findAll().forEach(e -> priceListDB.put(
        e.getStocksEntity().getSymbol(), e.getMarketPrice()));
      this.redisManager.set("stockList", priceListDB, Duration.ofMinutes(6)); */
      this.stockPriceRepository.findAll().forEach(e -> {
          if (e.getStocksEntity() != null) { 
              priceListDB.put(e.getStocksEntity().getSymbol(), e.getMarketPrice());
          }
      });
      this.redisManager.set("stockList", priceListDB, Duration.ofMinutes(6));
      System.out.println("stockprice save to redis");
      return priceListDB;
    }
  }

  //@PostConstruct
  public List<YahooStockDTO> getStockUpdateDTO(List<String> symbols) throws Exception{
      List<YahooStockDTO> dtolist = new ArrayList<>();
      MultiValueMap<String,String> DIYhttpParams = new LinkedMultiValueMap<>();
      String symbolParam = String.join(",", symbols);
      DIYhttpParams.put("symbols", List.of(symbolParam) );

      System.out.println("getQuote 4XX is" + dataAPIController.getQuote(symbols).getStatusCode().is4xxClientError());

      /*String api = "";
       try {
        if (dataAPIController.getQuote(symbols).getStatusCode().is2xxSuccessful()) {
          api = UriComponentsBuilder.newInstance().scheme("http")
        .host("localhost").port(8100).path("/api/no1/getData4")//.path("").path("")
        .queryParams(DIYhttpParams)
        .toUriString();
        }
       
      System.out.println(api);
      } catch (Exception e) {
        System.out.println("cannot connect getData4");
      } */

      String api = UriComponentsBuilder.newInstance().scheme("http")
        .host("localhost").port(8100).path("/api/no2/getData5")//.path("").path("")
        .queryParams(DIYhttpParams)
        .toUriString();
      System.out.println(api);

      try {
        //ResponseEntity<String> DIYresponse = restTemplate.getForEntity(api, String.class); //.getForObject(api, YahooStockDTO.class);
        ResponseEntity<List<YahooStockDTO>> DIYresponse = restTemplate.exchange(api,HttpMethod.GET, null, new ParameterizedTypeReference<List<YahooStockDTO>>() {}); //.getForObject(api, YahooStockDTO.class);
        //ResponseEntity<String> DIYresponse = restTemplate.getForEntity(api, String.class);
        if (DIYresponse.getStatusCode().is2xxSuccessful()) {
            return DIYresponse.getBody();
        }
        //ObjectMapper mapper = new ObjectMapper();

        //JsonNode root = mapper.readTree(DIYresponse.getBody());
        //JsonNode results = root.path("quoteResponse").path("result");
        //YahooStockApiResponse apiResponse = mapper.readValue(DIYresponse.getBody(), YahooStockApiResponse.class);
        //dtolist.add();
        return Collections.emptyList();
        // apiResponse.getQuoteResponse().getResult(); //mapper.readerForListOf(YahooStockDTO.class).readValue(results);
      } catch (Exception e) {
        System.out.println("Error" + e.getMessage());
        return Collections.emptyList();
      }

      //ResponseEntity<YahooStockDTO> DIYresponse = restTemplate.getForEntity(api, YahooStockDTO.class); //.getForObject(api, YahooStockDTO.class);

      //return Arrays.asList(a)dataAPIController.getQuote(symbols).getBody();
  }
  
  public List<YahooStockDTO> getStockDTO(List<String> symbols) throws Exception {
    List<YahooStockDTO> dtolist = new ArrayList<>();

    MultiValueMap<String,String> DIYhttpParams = new LinkedMultiValueMap<>();
      String symbolParam = String.join(",", symbols);
      DIYhttpParams.put("symbols", List.of(symbolParam) );
    
      
      //if (dataAPIController.getQuote(symbols).getStatusCode().is2xxSuccessful()) {
      /* System.out.println("getQuote 4XX is" + dataAPIController.getQuote(symbols).getStatusCode().is4xxClientError());
      String api = UriComponentsBuilder.newInstance().scheme("http")
        .host("localhost").port(8100).path("/api/no1/getData4")//.path("").path("")
        .queryParams(DIYhttpParams)
        .toUriString();

      YahooStockDTO DIYresponse = restTemplate.getForObject(api, YahooStockDTO.class);

      System.out.println(api); */
     // dtolist.add(dataAPIController.getQuote(symbols).getBody());

    //  return  dtolist; //DIYresponse; 
      
   // }

    crumbManager.getKey();
    String crumb = crumbManager.getStrFromgGetCrum();
    String cookie = crumbManager.getYahooCookie();
   

      MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
      //String symbolParam = String.join(",", symbols);
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

      //RestTemplate restTemplate = new RestTemplate();
      this.restTemplate = new RestTemplateBuilder() //
        .connectTimeout(Duration.ofSeconds(20)) //
        .readTimeout(Duration.ofSeconds(20)) //
        .build();
      ResponseEntity<YahooStockDTO> response = null;
      ResponseEntity<YahooStockDTO> updateresponse = dataAPIController.getQuote(symbols);
      do {
        
        response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockDTO.class);
       if (response.getBody() == null || response.getBody().getBody().getResults() == null) {
        while (response.getBody().getBody().getError().getCode().equals("Unauthorized")) {
          TimeUnit.SECONDS.sleep(30);
          getStockDTO(symbols);
        }
        
       }
        if (response.getBody().getBody().getResults() == null && 
          response.getBody().getBody().getError().getCode().equals("Unauthorized")) {
            while (response.getBody().getBody().getError().getCode().equals("Unauthorized")) {
              TimeUnit.SECONDS.sleep(30);
              getStockDTO(symbols);
            }
        }
      } while (!response.getStatusCode().is2xxSuccessful());

      //return response.getBody() != null ?  updateresponse.getBody() : response.getBody();
      System.out.println("updateresponse statuscode is " + updateresponse.getStatusCode());

      //return response.getStatusCode().is2xxSuccessful()? response.getBody() : updateresponse.getBody();
      dtolist.add(response.getBody());
      return dtolist;
  }

    //for rest time save to redis and show it to frontend
    public List<RedisQuickStore>  getData()throws JsonProcessingException {
      Boolean checkBreakTime = MarketTimeManager.isBreakTime(LocalTime.now());
      Boolean checktradeDay = MarketTimeManager.validTradeDay(LocalDate.now());
      List<RedisQuickStore> result = this.redisManager.get("DTOResult", new TypeReference<List<RedisQuickStore>>() {});
      //List<RedisQuickStore> dbStores =  result.isEmpty()  ? new ArrayList<>() : result;
      List<RedisQuickStore> dbStores =  new ArrayList<>() ;
      Optional<List<RedisQuickStore>> resultOpt = Optional.ofNullable(result);

      /* if (!dbStores.isEmpty()) {
        return dbStores;
        
      } */
      if (resultOpt.isPresent()) {
        return result;
      }
      
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


        /* if ( (!checkBreakTime && checktradeDay) || !checktradeDay) {
          //redis not null return reid first
         return result;
        } else  */
        //  if (!checkBreakTime && checktradeDay) {
         //   return Collections.emptyList();
         //}
         //if ( dbStores.isEmpty()) {
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
        
          System.out.println("working day update price");
      //}
      
      return dbStores;
      
    }

    public List<RedisQuickStore> getOHData() throws  JsonProcessingException {
     // Boolean checkBreakTime = MarketTimeManager.isBreakTime(LocalTime.now());
     // Boolean checktradeDay = MarketTimeManager.validTradeDay(LocalDate.now());
      
      //Map<String, List<RedisQuickStore>> redisOHresult = this.redisManager.g .get("DTOResultOH",new TypeReference<Map<String, List<RedisQuickStore>>>() {});
      List<RedisQuickStore> redisOHresult = this.redisManager.get("DTOResultOH", new TypeReference<List<RedisQuickStore>>() {});
      List<RedisQuickStore> dbStores = new ArrayList<>();

      if (redisOHresult != null ) {
        //redis not null return re first
        //result = redisOHresult.get("DTOResultOH");
       return redisOHresult;
      }
        if (redisOHresult == null || redisOHresult.isEmpty()) {
              //otherwise use DB data
          
    
          for (StockPriceOHEntity entity : this.stockPriceOHRepository.findAll()) {
            RedisQuickStore dStore = RedisQuickStore.builder().symbol(entity.getStocksEntity().getSymbol())
              .regularMarketPrice(entity.getMarketPrice())
              .regularMarketChangePercent(entity.getPerMarketChangePercent())
              .regularMarketOpen(entity.getOpen())
              .regularMarketDayHigh(entity.getHigh())
              .regularMarketDayLow(entity.getLow())
              .bid(null).ask(null).regularMarketTime(DateTimeManager.longToLocalDateTime(entity.getTradtimestamp()))
              .build();
            dbStores.add(dStore);
          }
          this.redisManager.set("DTOResultOH", dbStores);
            //return dbStores.isEmpty() ? Collections.emptyList() : dbStores;
    
        }
       
       

    return dbStores;

  }




  public static void main(String[] args) {
    LocalTime time = LocalTime.now();
    System.out.println(time);
  }
}
