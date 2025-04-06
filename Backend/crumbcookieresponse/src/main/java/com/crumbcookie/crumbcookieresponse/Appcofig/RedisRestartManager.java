package com.crumbcookie.crumbcookieresponse.Appcofig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;

@Component
@Order(1)
public class RedisRestartManager implements CommandLineRunner {

  @Autowired
  private  RedisManager redisManager;


  public RedisRestartManager(RedisManager redisManager) {
    this.redisManager = redisManager;
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Start Clean Redis...");
    redisManager.deleteAll();
    redisManager.getAll();
    System.out.println("redis content:" + redisManager.getAll().isEmpty());
  }



  
}
