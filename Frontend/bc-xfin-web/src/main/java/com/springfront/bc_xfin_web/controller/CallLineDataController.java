package com.springfront.bc_xfin_web.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.service.MarketDataService;

@RestController
public class CallLineDataController {
  @Autowired
  private MarketDataService marketDataService;

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping(value = "/getCallData")
  public List<MarketQuickStoreDTO> getQuickData() {
    return this.marketDataService.getmarketquickDTO();
  }

  @GetMapping(value = "/getStockLine")
  public List<LinePointDTO> getLineData() {
    return this.marketDataService.getLineData();
  }
}
