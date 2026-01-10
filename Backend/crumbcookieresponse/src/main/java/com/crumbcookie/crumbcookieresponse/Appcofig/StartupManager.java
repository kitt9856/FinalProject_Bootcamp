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
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;
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

  @Autowired
  private MarketTimeManager marketTimeManager;

  //開機後才執行stockUpdateDTO()，call自已的API，不再call request的json data
  @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() throws Exception {
        yahooAPIService.getStockUpdateDTO(this.stockStore.getTargetSybmols()); 
    }
  
  //開機時：
  //1.如果是工作天，刪除OHLC及Price DB舊資料
  //1.1如果是weekend，直接用現有資料(不刪除DB)
  //2.Crumb key
  //3.取得ExMarket資料(取延遲的K線資料for 陰燭圖)
  //4.取得Market資料(取當天即時價格資料 for 即時價格)
  //5.取得OHMarket資料(取當天即時OHLC資料) 
  @Override
  public void run(String... args) throws Exception {
    System.out.println("Service Start");
    this.marketTimeManager.workingDayDelOHPriceDB(this.stockPriceOHRepository);
    this.marketTimeManager.workingDayDelPriceDB(this.stockPriceRepository,this.stocksRepository);
    this.crumbManager.getKey();
    this.yahooExAPIService.getExMarket();
    this.stockListService.getMarket();
    this.yahooOHAPIService.getOHMarket();
    this.yahooAPIService.getOHData();
    System.out.println("Finish get price");
  }
  
}
