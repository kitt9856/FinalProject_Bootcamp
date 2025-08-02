package com.crumbcookie.crumbcookieresponse.Entity.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceOHEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.Repository.StocksRepository;
import com.crumbcookie.crumbcookieresponse.dto.RedisQuickStore;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockOHDTO;
import com.crumbcookie.crumbcookieresponse.lib.DateTimeManager;
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;



@Component
public class EntityMapper {

  @Autowired
  private MarketTimeManager marketTimeManager;

  /* @Autowired
  private StocksEntity stocksEntity; */

  @Autowired
  private  StocksRepository stocksRepository;
  

  public StocksEntity mapStockName(YahooStockDTO.QuoteBody.QuoteResult dto){
    return StocksEntity.builder().symbol(dto.getSymbol()).longName(dto.getLongName()).build();
  }

  public StocksEntity mapStockName(YahooStockOHDTO.Chart.OHResult OHdto){
    return StocksEntity.builder().symbol(OHdto.getMeta().getSymbol()).longName(OHdto.getMeta().getLongName()).build();
  }

  public StockPriceEntity mapStockPrice(YahooStockDTO.QuoteBody.QuoteResult dto){
    //LocalDate date = LocalDate.now();
    LocalTime time = longToLocalTime(dto.getRegularMarketTime());
    return StockPriceEntity.builder().marketPrice(dto.getRegularMarketPrice())
      .regularMarketChangePercent(dto.getRegularMarketChangePercent())
      .open(dto.getRegularMarketOpen())
      .high(dto.getRegularMarketDayHigh())
      .low(dto.getRegularMarketDayLow())
      .bid(dto.getBid()).ask(dto.getAsk())
      .tradeDate(MarketTimeManager.longToLocalDate(dto.getRegularMarketTime()))
      .tradeWeek(MarketTimeManager.longTOWeek(dto.getRegularMarketTime()))
      .priceUpdatetime(time).build();
  }

 /*  public List<StockPriceEntity> mapStockPrice(List<YahooStockDTO.QuoteBody.QuoteResult> dto){
    List<StockPriceEntity> stockPriceEntityList = new ArrayList<>();
    dto.stream().forEach(e -> stockPriceEntityList.add(mapStockPrice(e)));
    return stockPriceEntityList;
  } */

  public StockPriceEntity mapStockPrice(RedisQuickStore store){
    //LocalDate date = LocalDate.now();
    LocalTime time = (store.getRegularMarketTime().toLocalTime()) ;
    return StockPriceEntity.builder().marketPrice(store.getRegularMarketPrice())
      .regularMarketChangePercent(store.getRegularMarketChangePercent())
      .open(store.getRegularMarketOpen())
      .high(store.getRegularMarketDayHigh())
      .low(store.getRegularMarketDayLow())
      .bid(store.getBid()).ask(store.getAsk())
      .tradeDate((store.getRegularMarketTime()).toLocalDate())
      .tradeWeek((store.getRegularMarketTime().getDayOfWeek()).toString())
      .priceUpdatetime(time).build();
  }



  public LocalTime longToLocalTime(Long time) {
    Instant instant = Instant.ofEpochMilli(time * 1000);
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Hong_Kong"));
    LocalTime localTime = zonedDateTime.toLocalTime();
    return localTime.truncatedTo(ChronoUnit.HOURS)
          .plusMinutes((localTime.getMinute() / 5) * 5);
    //return localTime;

  }

  public Double getPriceChangePercent(Double MarketPrice, Double chartPreviousClose ) {
          if (MarketPrice == null || chartPreviousClose == null || chartPreviousClose == 0){
            return null;
          } else {
            BigDecimal bigDecPriceChange = (BigDecimal.valueOf(MarketPrice).subtract(BigDecimal.valueOf(chartPreviousClose)))
                      .divide(BigDecimal.valueOf(chartPreviousClose), 4 , RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                      .setScale(2, RoundingMode.HALF_UP);
            return  bigDecPriceChange.doubleValue();
                      
          }
        }

  public List<StockPriceOHEntity> mapPriceOH(YahooStockOHDTO.Chart ohDTO){
    List<StockPriceOHEntity> stockPriceOHEntityList = new ArrayList<>();
    List<YahooStockOHDTO.Chart.OHResult> results = ohDTO.getResult();
    List<String> stocks = stocksRepository.findAll().stream().filter( e -> 
      e != null).map(e -> e.getSymbol()).collect(Collectors.toList());
    YahooStockOHDTO.Chart.OHResult ohResult = results.stream().filter(
      e -> stocks.contains(e.getMeta().getSymbol()) //e.getMeta().getSymbol().equals(
    ).findFirst().get();
    StockPriceOHEntity stockPriceOHEntity = new StockPriceOHEntity();
    LocalDate tradeDate;
    Double dailyMarketprice = 0.00;
    Double open = 0.00;
    Double high = 0.00;
    Double low =  0.00;
   // Double close = 0.00;
    Double regularMarketChangePercent = 0.00;
    Long tradtimestamp = 0L;
    int tradeDateTimeslot =  ohResult.getTimestamp().size();
    System.out.println("getTimestamp size: " + tradeDateTimeslot);
    LocalDate firstrDate = DateTimeManager.longToLocalDate(ohResult.getMeta().getFirstTradeDate()) ;



    for (int i = 0; i < tradeDateTimeslot; i++) {
         
        tradeDate = firstrDate.plusDays(i);
        for ( YahooStockOHDTO.Chart.OHResult quote : results) {
          dailyMarketprice = quote.getIndicators().getQuote().get(0).getClose().get(i);
          open = quote.getIndicators().getQuote().get(0).getOpen().get(i);
          high = quote.getIndicators().getQuote().get(0).getHigh().get(i);
          low = quote.getIndicators().getQuote().get(0).getLow().get(i);
          if (i == 0) {
            regularMarketChangePercent = getPriceChangePercent(dailyMarketprice, quote.getMeta().getChartPreviousClose());
          } else {
            regularMarketChangePercent = getPriceChangePercent(dailyMarketprice,quote.getIndicators().getQuote().get(0).getClose().get(i - 1) );
          }
          tradtimestamp = quote.getTimestamp().get(i);
        }
          stockPriceOHEntity = StockPriceOHEntity.builder().marketPrice(dailyMarketprice)
          .tradeDate(tradeDate)
          .PerMarketChangePercent(regularMarketChangePercent)
          .open(open).close(dailyMarketprice)
          .high(high).low(low)
          .tradtimestamp(tradtimestamp)
          .build();
          stockPriceOHEntityList.add(stockPriceOHEntity);
        
    }

    return stockPriceOHEntityList;
  
  }
  
 
}
