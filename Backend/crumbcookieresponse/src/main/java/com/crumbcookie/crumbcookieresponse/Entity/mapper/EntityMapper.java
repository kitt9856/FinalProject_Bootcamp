package com.crumbcookie.crumbcookieresponse.Entity.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.crumbcookie.crumbcookieresponse.Entity.StockPriceEntity;
import com.crumbcookie.crumbcookieresponse.Entity.StocksEntity;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockDTO;
import com.crumbcookie.crumbcookieresponse.lib.MarketTimeManager;

@Component
public class EntityMapper {

  @Autowired
  private MarketTimeManager marketTimeManager;

  public StocksEntity mapStockName(YahooStockDTO.QuoteBody.QuoteResult dto){
    return StocksEntity.builder().symbol(dto.getSymbol()).build();

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



  public LocalTime longToLocalTime(Long time) {
    Instant instant = Instant.ofEpochMilli(time * 1000);
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Hong_Kong"));
    LocalTime localTime = zonedDateTime.toLocalTime();
    return localTime.truncatedTo(ChronoUnit.HOURS)
          .plusMinutes((localTime.getMinute() / 5) * 5);
    //return localTime;

  }
  
}
