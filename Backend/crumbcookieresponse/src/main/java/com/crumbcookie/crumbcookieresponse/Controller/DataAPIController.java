package com.crumbcookie.crumbcookieresponse.Controller;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/no1")
public class DataAPIController {
  @Autowired
  private CrumbManager crumbManager;

  @Autowired
  private RestTemplate restTemplate;

  

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
      System.out.println(headers);
      
      HttpEntity<String> entity = new HttpEntity<>(headers);

      RestTemplate restTemplate = new RestTemplate();

      restTemplate = new RestTemplateBuilder() //
        .connectTimeout(Duration.ofSeconds(40)) //
        .readTimeout(Duration.ofSeconds(40)) //
        .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
        .build();
       
      ResponseEntity<YahooStockDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockDTO.class);
      //ResponseEntity<String> rawResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

       //String responseBody = rawResponse.getBody();  
      /* if (responseBody == null || !responseBody.trim().endsWith("}")) {
          throw new RuntimeException("JSON response is truncated or empty");
      } */

      ObjectMapper mapper = new ObjectMapper();
      try {
        response = restTemplate.exchange(url, HttpMethod.GET, entity, YahooStockDTO.class);
        Thread.sleep(5000);
        // YahooStockDTO parsedDTO = mapper.readValue(responseBody, YahooStockDTO.class);
         /* return ResponseEntity.ok()
                .headers(rawResponse.getHeaders()) // 保留原始响应头
                .body(parsedDTO); */
        //return ResponseEntity.ok(result);
        return response;
      } catch (Exception e) {
        System.out.println(e);
      }

      return response;
      
    }

    


}
