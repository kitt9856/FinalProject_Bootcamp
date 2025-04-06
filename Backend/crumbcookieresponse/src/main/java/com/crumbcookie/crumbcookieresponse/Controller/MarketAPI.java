package com.crumbcookie.crumbcookieresponse.Controller;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/no2")
public class MarketAPI {

  @Autowired
  private StockListService stockListService;

  @Autowired
  private YahooAPIService yahooAPIService;

  @GetMapping("/getMarketData")
  public List<?> sendAPIData() throws JsonProcessingException {
    List<RedisQuickStore> data = this.yahooAPIService.getData();
    return data == null ? Collections.emptyList() : data;
  }
  
  
}
