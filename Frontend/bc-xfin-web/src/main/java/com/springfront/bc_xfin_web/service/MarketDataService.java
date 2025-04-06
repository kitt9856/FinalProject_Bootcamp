package com.springfront.bc_xfin_web.service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.dto.mapper.ChartMapper;
import com.springfront.bc_xfin_web.lib.DateManager;
import com.springfront.bc_xfin_web.model.CandleStick;

@Service
public class MarketDataService {
  
  /* @Autowired
  private DateManager dateManager; */

  @Value("${api.yahooFc.host}")
  private String host;

  @Value("${api.yahooFc.endpoints.markets}")
  private String endpointMarkets;

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

  public List<CandleStickDTO> getCandleData() {
    String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointMarkets)
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
   
      String url = UriComponentsBuilder.newInstance()
      .scheme("http")
      .host(host)
      .path(endpointMarkets)
      .build()
      .toUriString();
    
      MarketQuickStoreDTO[] backendData = restTemplate.getForObject(url, MarketQuickStoreDTO[].class);
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
}

