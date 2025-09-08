package com.crumbcookie.crumbcookieresponse.Appcofig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.crumbcookie.crumbcookieresponse.Repository.StockPriceOHRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.Service.YahooExAPIService;
import com.crumbcookie.crumbcookieresponse.Service.YahooOHAPIService;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.crumbcookie.crumbcookieresponse.model.StockStore;

@Component
@Order(2)
public class StartupManager implements CommandLineRunner {

  @Autowired
  private StocksRepository stocksRepository;

  @Autowired
  private StockPriceRepository stockPriceRepository;

  @Autowired
  private StockListService stockListService;

  @Autowired
  private  YahooOHAPIService yahooOHAPIService;

  @Autowired
  private RedisManager redisManager;

  @Autowired
  private StockPriceOHRepository stockPriceOHRepository;

  @Autowired
  private CrumbManager crumbManager;

  @Autowired
  private YahooAPIService yahooAPIService;

  @Autowired
  private StockStore stockStore;

  @Autowired
  private YahooExAPIService yahooExAPIService;

  @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() throws Exception {
        yahooAPIService.getStockUpdateDTO(this.stockStore.getTargetSybmols()); 
    }
  

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Service Start");
    this.stockPriceOHRepository.deleteAll();
    this.stockPriceRepository.deleteAll();
    this.stocksRepository.deleteAll();
    this.yahooExAPIService.getExMarket();
    this.stockListService.getMarket();
    this.yahooOHAPIService.getOHMarket();
    this.yahooAPIService.getOHData();
    this.crumbManager.getKey();
    System.out.println("Finish get price");
  }
  
}
