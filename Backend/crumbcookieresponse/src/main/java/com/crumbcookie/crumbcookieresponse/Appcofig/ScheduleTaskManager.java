package com.crumbcookie.crumbcookieresponse.Appcofig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.crumbcookie.crumbcookieresponse.Controller.DataAPIController;
import com.crumbcookie.crumbcookieresponse.Controller.MarketAPI;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceOHRepository;
import com.crumbcookie.crumbcookieresponse.Repository.StockPriceRepository;
import com.crumbcookie.crumbcookieresponse.Service.StockListService;
import com.crumbcookie.crumbcookieresponse.Service.YahooAPIService;
import com.crumbcookie.crumbcookieresponse.Service.YahooExAPIService;
import com.crumbcookie.crumbcookieresponse.Service.YahooOHAPIService;
import com.crumbcookie.crumbcookieresponse.lib.CrumbManager;
import com.crumbcookie.crumbcookieresponse.lib.RedisManager;
import com.crumbcookie.crumbcookieresponse.model.StockStore;

@Component
//@EnableScheduling
@Order(3)
public class ScheduleTaskManager {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private YahooAPIService yahooAPIService;

  @Autowired
  private YahooOHAPIService yahooOHAPIService;

  @Autowired
  private StockListService stockListService;

  @Autowired
  private StockPriceRepository stockPriceRepository;

  @Autowired
  private StockPriceOHRepository stockPriceOHRepository;

  @Autowired
  private RedisManager redisManager;

  @Autowired
  private CrumbManager crumbManager;

  @Autowired
  private DataAPIController dataAPIController;

  @Autowired
  private MarketAPI marketAPI;

  @Autowired
  private StockStore stockStore;

  @Autowired
  private YahooExAPIService yahooExAPIService;


  //weekday 9:35 to 15:55, every 2 min getdata
  //@Scheduled(cron = "0 35/2 9-16 * * MON-FRI",  zone = "Asia/Hong_Kong")
  //@Scheduled(cron = "*/30 * * * * MON-SAT",  zone = "Asia/Hong_Kong")
  //production
  @Scheduled(cron = "0 35-59/2 9 * * MON-FRI", zone = "Asia/Hong_Kong")
  @Scheduled(cron = "0 */2 10-15 * * MON-FRI", zone = "Asia/Hong_Kong")
  @Scheduled(cron = "0 0-15/2 16 * * MON-FRI", zone = "Asia/Hong_Kong")
  //test
  //@Scheduled(cron = "0 */2 00-15 * * MON-FRI", zone = "Asia/Hong_Kong")
  public void updateMarket() throws Exception {
    this.stockListService.getMarket();
    this.yahooAPIService.getData();
    
    System.out.println("api is" + this.stockListService.getMarket().isEmpty());
  }

  //開市前更新OHLC資料
  @Scheduled(cron = "0 35 9 * * MON-FRI", zone = "Asia/Hong_Kong")
  public void updateOHMarket() throws Exception{
    if (this.marketAPI.sendAPIData().isEmpty()) {
      this.yahooOHAPIService.getOHMarket();
    }
  }


  //@Scheduled(cron = "0 30-59 8 * * MON-FRI", zone = "Asia/Hong_Kong")
  //@Scheduled(cron = "0 0-30 9 * * MON-FRI", zone = "Asia/Hong_Kong")
  @Scheduled(cron = "0 30 9 * * MON-FRI", zone = "Asia/Hong_Kong")
  public void prevMarket() throws Exception {
    this.stockPriceOHRepository.deleteAll();
    this.stockPriceRepository.deleteAll();
    this.redisManager.deleteAll();
    this.crumbManager.getKey();
  }

  //@Scheduled(fixedRate = 30 * 60 * 1000)
  //@Scheduled(cron = "0 */10 * * * *",  zone = "Asia/Hong_Kong")
  /* public void ping() throws Exception{
    restTemplate.getForObject("https://www.ckopenpj.dpdns.org/stocklinechart", String.class);
    System.out.println("ping Frontend now");
    //this.marketAPI.ping();
  } */
  
  
}
