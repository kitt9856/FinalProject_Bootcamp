package com.crumbcookie.crumbcookieresponse.Service;

import java.time.Duration;
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

import com.crumbcookie.crumbcookieresponse.Entity.mapper.EntityMapper;
import com.crumbcookie.crumbcookieresponse.dto.ExQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockOpenDTO;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.crumbcookie.crumbcookieresponse.model.StockStore;

@Service
public class YahooExAPIService {
    @Autowired
    private CrumbManager crumbManager;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private RedisManager redisManager;


    @Autowired
    private StockStore stockStore;



    public YahooStockOpenDTO getExStockDTO(String symbol, String range, String interval) throws Exception{
        crumbManager.getKey();
        String crumb = crumbManager.getStrFromgGetCrum();
        String cookie = crumbManager.getYahooCookie();
   

        MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
        String symbolParam = symbol;
        

        String[] intervals = {"1m", "2m", "5m", "15m", "30m", "60m", "90m", "1h", "4h", "1d", "5d", "1wk", "1mo", "3mo"};
        List<String> intervalList = List.of(intervals);
        String intervalParam = intervalList.contains(interval) ? interval : "";

        //httpParams.put("symbols", List.of(symbolParam) );
        httpParams.put("range", List.of(range));
        httpParams.put("interval", List.of(interval));
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

        System.out.println("Ex Socure URL: " + headers);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate = new RestTemplateBuilder() //
            .connectTimeout(Duration.ofSeconds(20)) //
            .readTimeout(Duration.ofSeconds(20)) //
            .build();
        
        ResponseEntity<YahooStockOpenDTO> response = null;
        //String errorcode = String.join(",", "Bad Request");
        String errorcode = String.join(",", "Bad Request");

        boolean isbadreq = false;

        do {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockOpenDTO.class);
            /* isbadreq = response.getBody().getChart().getError().getCode().equals(errorcode) ? 
            true : false; */
            if (response.getBody() == null || response.getBody().getChart() == null) {
                while (isbadreq) { 
                    if (response.getBody().getChart().getResult() != null) {
                        TimeUnit.SECONDS.sleep(30);
                        getExStockDTO(symbol, range, interval);
                        isbadreq = true;

                    }
                }
            }
        
            if (response.getBody().getChart().getResult() == null && isbadreq ){
                    while (isbadreq) { 
                        TimeUnit.SECONDS.sleep(10);
                        getExStockDTO(symbol, range, interval);

                    }
                }

        } while (!response.getStatusCode().is2xxSuccessful()  );

        return  response.getBody();

    }

    public List<YahooStockOpenDTO> getExMarket() throws Exception{
        List<String>  symbols =  stockStore.getTargetSybmols();
        String range = "1d";
        String interval = "1m";

        List<YahooStockOpenDTO> dtolist = new ArrayList<>();
        List<ExQuickStore> stores = new ArrayList<>();
        //List<ExQuickStore> redisExData = this.redisManager.get("YahooExDTOResult", new TypeReference<List<ExQuickStore>>() {});
        

        for (String string : symbols){
           YahooStockOpenDTO dto = this.getExStockDTO(string, range, interval);
           dtolist.add(dto); 
        }
        

        //List<ExQuickStore> stores = new ArrayList<>();

        for (YahooStockOpenDTO exdto : dtolist) {
            for (YahooStockOpenDTO.Chart.Result result : exdto.getChart().getResult()) {
                stores.add(this.entityMapper.mapExQuickStore(result));
            }
        }
        this.redisManager.set("exOpenDTO", dtolist, Duration.ofDays(2));
        this.redisManager.set("ExDTOResult", stores,Duration.ofDays(2));


        return dtolist;


    }


    
    
}
