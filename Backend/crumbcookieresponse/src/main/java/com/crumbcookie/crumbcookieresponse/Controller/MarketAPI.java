package com.crumbcookie.crumbcookieresponse.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.model.StockStore;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/no2")
public class MarketAPI {

  @Autowired
  private StockListService stockListService;

  @Autowired
  private YahooAPIService yahooAPIService;

  @Autowired
  private StocksRepository stocksRepository;

  @Autowired
  private StockStore stockStore;

  @GetMapping("/getMarketData")
  public List<?> sendAPIData() throws JsonProcessingException {
    List<RedisQuickStore> data = this.yahooAPIService.getData();
    return data == null ? Collections.emptyList() : data;
  }

  //for debug checking
  @GetMapping("/getData6")
    public List<YahooStockDTO> getMarketUpdateData(@RequestParam List<String> symbols) throws Exception {
      //return stockListService.getMarket();
      List<YahooStockDTO> data = yahooAPIService.getStockUpdateDTO(symbols);
      return data;
    }


  @GetMapping("/getData5")
    public List<YahooStockDTO> getMarketData(@RequestParam List<String> symbols) throws Exception {
      //return stockListService.getMarket();
      List<YahooStockDTO> data = yahooAPIService.getStockDTO(symbols);
      return data;
    }

  @GetMapping("/getMarketOHData")
  public List<?> sendAPIOHData() throws JsonProcessingException {
    List<RedisQuickStore> OHdata = this.yahooAPIService.getOHData();
    return OHdata == null ? Collections.emptyList() : OHdata;
  }

  @GetMapping("/search")
  public  List<?> searchStock(@RequestParam String keyword)  {
    return this.stocksRepository.findByKeyword(keyword);
  }

  @GetMapping("/checkAllSybmol")
  public  List<String> checkAllSybmol() throws  Exception {
    return this.yahooAPIService.getSymbols();
  }

  @GetMapping("/checkAllSybmolName")
  public  Map<String,String> checkAllSybmolName() throws  Exception {
    return this.yahooAPIService.getSymbolsName();
  }




  
  
}
