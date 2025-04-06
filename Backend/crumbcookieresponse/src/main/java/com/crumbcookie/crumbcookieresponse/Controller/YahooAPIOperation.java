package com.crumbcookie.crumbcookieresponse.Controller;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Schema;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO.YahooStockDTOBuilder;
import com.crumbcookie.crumbcookieresponse.dto.mapper.YahooStockmapper;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.Getsource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.PathParam;

@RestController
public class YahooAPIOperation {

  @Value("${api.jph.domain}")
  private String domain;

  @Value("${api.jph.quote.type}")
  private String type;

  @Value("${api.jph.quote.version}")
  private String version;

  @Value("${api.jph.quote.endpoint}")
  private String endpoint;

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  private CrumbManager crumbManager; 

  @GetMapping(value = "/getData3")
  public YahooStockDTO getURLPage(@RequestParam List<String> symbols )
  throws JsonProcessingException{
    
      //CrumbManager crumbManager = new CrumbManager();
      try {
        this.crumbManager.setYahCookie();
        this.crumbManager.getKey();
        
      } catch (Exception e) {
        System.out.println("Error");
      }
      String crumb = this.crumbManager.getStrFromgGetCrum();
      MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
      String symbolParam = String.join(",", symbols);
      httpParams.put("symbols", List.of(symbolParam) );
      httpParams.put("crumb", List.of(crumb));
  
      String url = UriComponentsBuilder.newInstance().scheme("https")
        .host(this.domain).path(this.version).path(this.type).path(this.endpoint)
        .queryParams(httpParams)
        .toUriString();
      System.out.println(url);

      HttpHeaders headers =new HttpHeaders();
      //headers.set("Cookie", null);
    //  headers.set("User-Agent", null);
      headers.set(HttpHeaders.COOKIE, this.crumbManager.getYahooCookie());
      System.out.println(this.crumbManager.getYahooCookie());
      headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");
      //HttpEntity<String> entity = new HttpEntity<>(headers);
      HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(httpParams, headers);
  

      ResponseEntity<String> response = this.restTemplate.exchange(url,HttpMethod.GET, entity ,String.class);

      YahooStockDTO urlData = this.restTemplate.getForObject(response.getBody(), YahooStockDTO.class);

      return urlData;
  }

 

  @GetMapping(value = "/getData")
  public YahooStockDTO getStock(@RequestParam List<String> symbols )
    throws JsonProcessingException{
    //CrumbManager crumbManager = new CrumbManager();
    try {
      crumbManager.setYahCookie();
      crumbManager.getKey();
      
    } catch (Exception e) {
      System.out.println("Error");
    }
    String crumb = crumbManager.getStrFromgGetCrum();
    System.out.println(crumb);
    //queryParams need (MultiValueMap<?>)
    MultiValueMap<String,String> httpParams = new LinkedMultiValueMap<>();
    String symbolParam = String.join(",", symbols);
    httpParams.put("symbols", List.of(symbolParam) );
    httpParams.put("crumb", List.of(crumb));

    String url = UriComponentsBuilder.newInstance().scheme("https")
      .host(this.domain).path(this.version).path(this.type).path(this.endpoint)
      .queryParams(httpParams)
      .toUriString();
    System.out.println(url);

    //YahooStockDTO stockDTO = this.restTemplate.getForObject(url, YahooStockDTO.class);

   /*  HttpHeaders headers =new HttpHeaders();
    //headers.set("Cookie", null);
    headers.set("User-Agent", null);
    headers.set(HttpHeaders.COOKIE, crumbManager.getYahooCookie());
    System.out.println(crumbManager.getYahooCookie());
    headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");
    HttpEntity<String> entity = new HttpEntity<>(headers); */

    ResponseEntity<String> response = this.restTemplate.getForEntity(url,String.class);

    /* YahooStockDTO stockDTO = new ObjectMapper().readValue(response.getBody(), 
      YahooStockDTO.class); */
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      try {
    YahooStockDTO stockDTO = new ObjectMapper().readValue(response.getBody(), YahooStockDTO.class);
        
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    YahooStockDTO stockDTO = new ObjectMapper().readValue(response.getBody(), YahooStockDTO.class);
    System.out.println(stockDTO);

    return stockDTO;

  }

  @GetMapping(value = "/getData2")
  public YahooStockDTO getStockData(@RequestParam List<String> symbols) {
    //CrumbManager crumbManager = new CrumbManager();
    String crumb = null;
    try {
        this.crumbManager.setYahCookie();
        this.crumbManager.getKey();
        
    } catch (Exception e) {
        System.out.println("Error getting crumb value");
    }
    crumb = this.crumbManager.getStrFromgGetCrum();
    System.out.println(crumb);
    String url = "https://query1.finance.yahoo.com/v7/finance/quote";

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
            .queryParam("symbols", String.join(",", symbols))
            .queryParam("crumb", crumb);

    String fullUrl = builder.toUriString();

    // Send GET request and map the response to YahooStockDTO
    String response = restTemplate.getForObject(fullUrl, String.class);

    // Convert JSON response to YahooStockDTO object
    ObjectMapper objectMapper = new ObjectMapper();
    YahooStockDTO stockDTO = null;
    try {
        stockDTO = objectMapper.readValue(response, YahooStockDTO.class);
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }

    return stockDTO;
}


}
