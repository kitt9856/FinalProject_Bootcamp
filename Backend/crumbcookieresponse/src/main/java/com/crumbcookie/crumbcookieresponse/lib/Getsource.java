package com.crumbcookie.crumbcookieresponse.lib;

import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface Getsource {
  


  
 // RestTemplate getRestTemplate();

 /*  public Getsource() {
    this.restTemplate = new RestTemplateBuilder() //
        .connectTimeout(Duration.ofSeconds(5)) //
        .readTimeout(Duration.ofSeconds(5)) //
        .build();
   // this.restTemplate = new RestTemplate();
} */

 /* default  YahooStockDTO geStockDTO(List<String> symbols) throws JsonProcessingException {

    try {
      Thread.sleep(10000); // 延遲 1 秒
  } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
  }
    
    CrumbManager crumbManager = new CrumbManager();
      try {
        crumbManager.setYahCookie();
        crumbManager.getKey();
        
      } catch (Exception e) {
        System.out.println("Error");
      }
      String crumb = crumbManager.getStrFromgGetCrum();
      MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
      String symbolParam = String.join(",", symbols);
      httpParams.put("symbols", List.of(symbolParam) );
      httpParams.put("crumb", List.of(crumb));

      String url = UriComponentsBuilder.newInstance().scheme("https")
        .host("query1.finance.yahoo.com").path("/v7").path("/finance").path("/quote")
        .queryParams(httpParams)
        .toUriString();
      System.out.println(url);

      ResponseEntity<String> response = getRestTemplate().getForEntity(url,String.class);

      YahooStockDTO urlData = getRestTemplate().getForObject(response.getBody(), YahooStockDTO.class);

      return urlData;


  } */


}
