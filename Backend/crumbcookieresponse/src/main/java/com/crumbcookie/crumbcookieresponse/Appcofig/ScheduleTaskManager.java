package com.crumbcookie.crumbcookieresponse.Appcofig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;

@Component
@Order(2)
public class ScheduleTaskManager {

  @Autowired
  private YahooAPIService yahooAPIService;

  @Autowired
  private StockListService stockListService;

  @Autowired
  private StockPriceRepository stockPriceRepository;

  @Autowired
  private RedisManager redisManager;

  //weekday 9:35 to 15:55, every 5 min getdata
  @Scheduled(cron = "0 35/5 9-16 * * MON-FRI",  zone = "Asia/Hong_Kong")
  public void updateMarket() throws Exception {
    this.stockListService.getMarket();
    this.yahooAPIService.getPriceofStock();
    this.yahooAPIService.getData();
  }

  /* @Scheduled(cron = "0 0 16 * * MON-FRI",  zone = "Asia/Hong_Kong")
  public void closeMarket() throws Exception {
    this.yahooAPIService.getPriceofStock();
  } */


  @Scheduled(cron = "0 30-59 8 * * MON-FRI", zone = "Asia/Hong_Kong")
  @Scheduled(cron = "0 0-30 9 * * MON-FRI", zone = "Asia/Hong_Kong")
  public void prevMarket() throws Exception {
    this.stockPriceRepository.deleteAll();
   // this.redisManager.deleteAll();
  }
  
  
}
