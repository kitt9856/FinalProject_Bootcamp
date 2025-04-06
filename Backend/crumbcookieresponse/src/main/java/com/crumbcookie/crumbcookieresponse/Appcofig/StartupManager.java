package com.crumbcookie.crumbcookieresponse.Appcofig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;

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
  private RedisManager redisManager;

  

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Service Start");
    
    this.stockPriceRepository.deleteAll();
    this.stocksRepository.deleteAll();
    this.stockListService.getMarket();
    System.out.println("Finish get price");
  }
  
}
