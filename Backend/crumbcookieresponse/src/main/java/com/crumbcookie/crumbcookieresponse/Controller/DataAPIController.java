package com.crumbcookie.crumbcookieresponse.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/no1")
public class DataAPIController {
  @Autowired
  private CrumbManager crumbManager;

  @GetMapping("/getData4")
  public ResponseEntity<YahooStockDTO> getQuote(@RequestParam List<String> symbols)
    throws Exception{
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
      
      HttpEntity<String> entity = new HttpEntity<>(headers);

      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<YahooStockDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockDTO.class);

      return response;
      
    }
}
