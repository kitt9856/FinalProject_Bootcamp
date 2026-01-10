package com.crumbcookie.crumbcookieresponse.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import com.crumbcookie.crumbcookieresponse.Entity.StockPriceOHEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.Entity.mapper.EntityMapper;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceOHRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockOHDTO;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.DateTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.crumbcookie.crumbcookieresponse.model.StockStore;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class YahooOHAPIService {

    @Autowired
    private CrumbManager crumbManager;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StocksRepository stocksRepository;
  
    @Autowired
    private StockPriceOHRepository PriceOHRepo;

    @Autowired
    private YahooAPIService yahooAPIService;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private DateTimeManager dateTimeManager;

    @Autowired
    private StockStore stockStore;

    public YahooStockOHDTO getOHStockDTO(String symbol, Long fromDate, Long toDate, String interval) throws Exception{
        crumbManager.getKey();
        String crumb = crumbManager.getStrFromgGetCrum();
        String cookie = crumbManager.getYahooCookie();
   

        MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
        String symbolParam = String.join(",", symbol);
        String fromDateStr = String.valueOf(fromDate);
        String toDateStr = String.valueOf(toDate);

        String[] intervals = {"1d","5d","1mo","3mo","6mo","1y","2y","5y","10y","ytd","max"};
        List<String> intervalList = List.of(intervals);
        String intervalParam = intervalList.contains(interval) ? interval : "";

        //httpParams.put("symbols", List.of(symbolParam) );
        httpParams.put("period1", List.of(fromDateStr));
        httpParams.put("period2", List.of(toDateStr));
        httpParams.put("interval", List.of(interval));
        httpParams.put("events", List.of("history"));
        httpParams.put("crumb", List.of(crumb));

        String url = UriComponentsBuilder.newInstance().scheme("https")
        .host("query1.finance.yahoo.com").path("/v8").path("/finance").path("/chart/").path(symbolParam)
        .queryParams(httpParams)
        .toUriString();
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        headers.set("User-Agent", "Mozilla/5.0");
        headers.set("Crumb", crumb);

        System.out.println("OH Socure URL: " + headers);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate = new RestTemplateBuilder() //
            .connectTimeout(Duration.ofSeconds(20)) //
            .readTimeout(Duration.ofSeconds(20)) //
            .build();
        
        ResponseEntity<YahooStockOHDTO> response = null;
        //String errorcode = String.join(",", "Bad Request");
        String errorcode = String.join(",", "Bad Request");

        boolean isbadreq = false;

        do {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockOHDTO.class);
            
            if (response.getBody() == null || response.getBody().getChart() == null) {
                while (isbadreq) { 
                    if (response.getBody().getChart().getResult() != null) {
                        TimeUnit.SECONDS.sleep(30);
                        getOHStockDTO(symbol, fromDate, toDate, interval);
                        isbadreq = true;

                    }
                }
            }
        
            if (response.getBody().getChart().getResult() == null && isbadreq ){
                    while (isbadreq) { 
                        TimeUnit.SECONDS.sleep(10);
                        getOHStockDTO(symbol, fromDate, toDate, interval);
                    }
                }

        } while (!response.getStatusCode().is2xxSuccessful()  );

        return  response.getBody();

    }


    public List<YahooStockOHDTO> getOHMarket() throws  Exception{
        List<String>  symbols =  stockStore.getTargetSybmols();
        String marketTime = "1d";
        LocalDate today = LocalDate.now(this.dateTimeManager.getZoneId());
        Long fromDate = this.dateTimeManager.convert(today);
        LocalDate topastDate = today.minusMonths(3);
        Long toDate = this.dateTimeManager.convert(topastDate);
       // Long fromDate = this.dateTimeManager.convert(DateTimeManager.longToLocalDate(toDate).plusMonths(3));

       List<YahooStockOHDTO> ohDTOList = new ArrayList<>();
        
       for (String string : symbols) {
        YahooStockOHDTO ohdto = this.getOHStockDTO(string, toDate, fromDate, marketTime);
        ohDTOList.add(ohdto);
       }

        //YahooStockOHDTO ohdto = this.getOHStockDTO(symbols, toDate, fromDate, marketTime);
        List<RedisQuickStore> redisOHDtoResult = new ArrayList<>();

        List<RedisQuickStore> redisOHData = this.redisManager.get("OHDTOResult", new TypeReference<List<RedisQuickStore>>() {});
        if (redisOHData != null ) {
            return ohDTOList;
        }

        StocksEntity stocksEntity = new  StocksEntity();
        StockPriceOHEntity stockPriceOHEntity = new StockPriceOHEntity();
        List<StockPriceOHEntity> stockPriceOHEntityList = new ArrayList<>();
        
      

        for (YahooStockOHDTO ohdto : ohDTOList) {
            for(YahooStockOHDTO.Chart.OHResult result : ohdto.getChart().getResult()){
                stocksEntity = this.entityMapper.mapStockName(result);
             stockPriceOHEntityList = this.entityMapper.mapPriceOH(ohdto.getChart());
            
            stockPriceOHEntity.setStocksEntity(stocksEntity);
                for (StockPriceOHEntity OHentity : stockPriceOHEntityList) {
                    stocksEntity = this.stocksRepository.findBySymbol(result.getMeta().getSymbol())
                    .orElseGet(() -> {
                        StocksEntity newStock = new StocksEntity();
                        newStock.setSymbol(result.getMeta().getSymbol());
                return stocksRepository.save(newStock);
            });
                    OHentity.setStocksEntity(stocksEntity);
                    this.PriceOHRepo.save(OHentity);

                }
            }
             
        }

        for (StockPriceOHEntity entity : PriceOHRepo.findAll()) {
            RedisQuickStore redisQuickStore = RedisQuickStore.builder()
            .symbol(entity.getStocksEntity().getSymbol())
            .regularMarketPrice(entity.getMarketPrice())
            .regularMarketTime(DateTimeManager.longToLocalDateTime(entity.getTradtimestamp()))
            .regularMarketChangePercent(entity.getPerMarketChangePercent())
            .regularMarketOpen(entity.getOpen())
            .regularMarketDayHigh(entity.getHigh())
            .regularMarketDayLow(entity.getLow())
            .build();
            redisOHDtoResult.add(redisQuickStore);
        }

        System.out.println("DTO data set to redis" + "From Date= " + DateTimeManager.longToLocalDate(fromDate) 
            + " , " + "To Date= " + DateTimeManager.longToLocalDate(toDate));

        this.redisManager.set("OHDTOResult", redisOHDtoResult);

        
        return  ohDTOList;

    }

   /*  public static void main(String[] args) {
        
        String crumb = "viya008zDer";
   

        MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
        String symbolParam = String.join(",", "0388.HK");
        String fromDateStr = String.valueOf(1388563200);
        String toDateStr = String.valueOf(1509694074);

        String[] intervals = {"1d","5d","1mo","3mo","6mo","1y","2y","5y","10y","ytd","max"};
        List<String> intervalList = List.of(intervals);
        String intervalParam = "1d";

        //httpParams.put("symbols", List.of(symbolParam) );
        httpParams.put("period1", List.of(fromDateStr));
        httpParams.put("period2", List.of(toDateStr));
        httpParams.put("interval", List.of("1d"));
        httpParams.put("events", List.of("history"));
        httpParams.put("crumb", List.of(crumb));

        String url = UriComponentsBuilder.newInstance().scheme("https")
        .host("query1.finance.yahoo.com").path("/v8").path("/finance").path("/chart/").path(symbolParam)
        .queryParams(httpParams)
        .toUriString();
        System.out.println(url);

    }
 */
}
