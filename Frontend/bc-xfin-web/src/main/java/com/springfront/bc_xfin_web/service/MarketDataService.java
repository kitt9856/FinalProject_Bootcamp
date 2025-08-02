package com.springfront.bc_xfin_web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.dto.Symbol;
import com.springfront.bc_xfin_web.dto.mapper.ChartMapper;

@Service
public class MarketDataService {
  
  /* @Autowired
  private DateManager dateManager; */

  @Value("${api.yahooFc.host}")
  private String host;

  @Value("${api.yahooFc.endpoints.markets}")
  private String endpointMarkets;

  @Value("${api.yahooFc.endpoints.ohmarkets}")
  private String endpointOHMarkets;

  @Value("${api.yahooFc.endpoints.search}")
  private String search;

  @Value("${api.yahooFc.endpoints.checkAllSybmol}")
  private String AllSybmol;

  @Value("${api.yahooFc.endpoints.checkAllSybmolName}")
  private String AllSybmolName;



  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private  ChartMapper chartMapper;

  

  

  public List<MarketQuickStoreDTO> getmarketquickDTO(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointMarkets)
      .build()
      .toUriString();
    
      MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class);

      List<MarketQuickStoreDTO> dtodata = new ArrayList<>();

      for (MarketQuickStoreDTO marketQuickStoreDTO : backendData) {
        MarketQuickStoreDTO dtobuilder = this.chartMapper.dtomap(marketQuickStoreDTO);
        dtodata.add(dtobuilder);
      }

      return dtodata;

  }

  public List<MarketQuickStoreDTO> getOHmarketquickDTO(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointOHMarkets)
      .build()
      .toUriString();
    
      MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class);

      List<MarketQuickStoreDTO> dtodata = new ArrayList<>();

      for (MarketQuickStoreDTO marketQuickStoreDTO : backendData) {
        MarketQuickStoreDTO dtobuilder = this.chartMapper.dtomap(marketQuickStoreDTO);
        dtodata.add(dtobuilder);
      }

      return dtodata;

  }

  public List<CandleStickDTO> getCandleData() {
    /* String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointMarkets)
      .build()
      .toUriString();

    MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class); */
    List<MarketQuickStoreDTO> backendData = getmarketquickDTO();


    
    List<CandleStickDTO> candleStickDTOs = new ArrayList<>();

    for (MarketQuickStoreDTO dto : backendData) {
      if (dto.getRegularMarketOpen() == null || dto.getRegularMarketDayHigh() == null 
        || dto.getRegularMarketDayLow() == null || dto.getRegularMarketPrice() == null) {
        continue;
    }
      MarketQuickStoreDTO dtobuilder =  dto; //this.chartMapper.dtomap(dto);
      CandleStickDTO candleStickDTO = this.chartMapper.mapCandle(dtobuilder);
      candleStickDTOs.add(candleStickDTO);
    }

    return candleStickDTOs;

  }

  public List<CandleStickDTO> getOHCandleData() {
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointOHMarkets)
      .build()
      .toUriString();

    MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class);
    
    List<CandleStickDTO> candleStickDTOs = new ArrayList<>();

    for (MarketQuickStoreDTO dto : backendData) {
      MarketQuickStoreDTO dtobuilder = this.chartMapper.dtomap(dto);
      CandleStickDTO candleStickDTO = this.chartMapper.mapCandle(dtobuilder);
      candleStickDTOs.add(candleStickDTO);
    }

    return candleStickDTOs;

  }

  public List<LinePointDTO> getLineData()  {
   
      /* String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointMarkets)
      .build()
      .toUriString();
    
      MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class); */
      List<MarketQuickStoreDTO> backendData = getmarketquickDTO();
     /*  MarketQuickStoreDTO[] backendData = Optional.ofNullable(
        restTemplate.getForObject(url, MarketQuickStoreDTO[].class)
    ).orElse(new MarketQuickStoreDTO[0]); */ 
   /*  MarketQuickStoreDTO[] backendData;
    try {
        backendData = Optional.ofNullable(
                restTemplate.getForObject(url, MarketQuickStoreDTO[].class)
            ).orElse(new MarketQuickStoreDTO[0]);
    } catch (RestClientException e) {
        // 记录日志并返回空列表
        System.out.println("API请求失败: {}" + url + e);
        return Collections.emptyList();
    } */


      List<LinePointDTO> linedto = new ArrayList<>();

      for (MarketQuickStoreDTO dto : backendData) {
          MarketQuickStoreDTO dtobuilder = this.chartMapper.dtomap(dto);
          LinePointDTO linePointDTO = this.chartMapper.mapLine(dtobuilder);
          linedto.add(linePointDTO);
      }

      /* for (MarketQuickStoreDTO dto : backendData) {
        if (dto != null) {
            try {
                linedto.add(this.chartMapper.map(dto));
            } catch (Exception e) {
                System.out.println("fail");
            }
        }
    } */

     return  linedto;
    
    
  }

  public List<LinePointDTO> getOHLineData()  {
   
      String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointOHMarkets)
      .build()
      .toUriString();
    
      MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class);

      List<LinePointDTO> linedto = new ArrayList<>();

      for (MarketQuickStoreDTO dto : backendData) {
          MarketQuickStoreDTO dtobuilder = this.chartMapper.dtomap(dto);
          LinePointDTO linePointDTO = this.chartMapper.mapLine(dtobuilder);
          linedto.add(linePointDTO);
      }

     return  linedto;
    
  }

  public List<String> getAllSymbols(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(AllSybmol)
      .build()
      .toUriString();

    String[] symbols = restTemplate.getForObject(url, String[].class);

    
    return Arrays.asList(symbols);
  }

  public Map<String,String> getAllSymbolsName(){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(AllSybmolName)
      .build()
      .toUriString();

    ParameterizedTypeReference<Map<String, String>> typeRef =new ParameterizedTypeReference<Map<String, String>>() {};

    ResponseEntity<Map<String, String>> response =restTemplate.exchange(url, HttpMethod.GET, null, typeRef);

    Map<String,String> dtos = response.getBody();


    
    return dtos;
  }

 /*  public List<MarketQuickStoreDTO> getByKeyword(String keyword){
    List<MarketQuickStoreDTO> symbolsDTO = new ArrayList<>();
    for (MarketQuickStoreDTO marketQuickStoreDTO : symbolsDTO) {
      if (marketQuickStoreDTO.getSymbol().contains(keyword)) {
        symbolsDTO.add(marketQuickStoreDTO);
      }
    }
    return symbolsDTO;
  } */

  public List<MarketQuickStoreDTO> getByKeyword(String keyword){
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(search)
      .queryParam("keyword", keyword)
      .build()
      .toUriString();

    Symbol[] searchOutput = restTemplate.getForObject(url, Symbol[].class,keyword);

    //List<MarketQuickStoreDTO> symbolList = getAllSymbols();
    List<MarketQuickStoreDTO> symbolList = getmarketquickDTO();
    List<MarketQuickStoreDTO> results = new ArrayList<>();
    Map<String,String> SymbolsNameList = getAllSymbolsName();


    if (searchOutput != null && searchOutput.length > 0) {
      for (MarketQuickStoreDTO dto : symbolList) {
          if (dto.getSymbol().equals(searchOutput[0].getSymbol()) || dto.getSymbolFullName().equals(searchOutput[0].getLongName()) ) {
            results.add(dto);
            break;
          }
      }
    }

    
    return results;
  }

  /* public static void main(String[] args) {
    RestTemplate restTemplate = new RestTemplate();


    String keyword = "038";

    String url = "http://localhost:8100/api/no2/checkAllSybmol";
    String[] backendData = restTemplate.getForObject(url, String[].class);
    System.out.println(Arrays.asList(backendData).toString());
  } */

  




}

