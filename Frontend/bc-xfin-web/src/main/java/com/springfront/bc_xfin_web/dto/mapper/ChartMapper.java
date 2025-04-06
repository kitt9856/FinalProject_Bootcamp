package com.springfront.bc_xfin_web.dto.mapper;

import org.springframework.stereotype.Component;
import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.lib.DateManager;
import com.springfront.bc_xfin_web.lib.DateManager.Zone;
import com.springfront.bc_xfin_web.model.CandleStick;
import com.springfront.bc_xfin_web.model.LinePoint;

@Component
public class ChartMapper {
  
  public CandleStickDTO map(CandleStick candleStick) {
    long unixtimeDate  = DateManager.of(Zone.HK).convert(candleStick.getDate());
    return CandleStickDTO.builder().date(unixtimeDate)
      .open(candleStick.getOpen()).close(candleStick.getClose())
      .high(candleStick.getHigh()).low(candleStick.getLow())
      .symbol(candleStick.getSymbol())
      .build();
  }

  public CandleStickDTO mapCandle(MarketQuickStoreDTO quickDto) {
    long unixtimeDatetime =
        DateManager.of(Zone.HK).convert(quickDto.getRegularMarketTime());
    return CandleStickDTO.builder().symbol(quickDto.getSymbol())
        .date(unixtimeDatetime)
        .open(quickDto.getRegularMarketOpen())
        .close(quickDto.getRegularMarketPrice())
        .high(quickDto.getRegularMarketDayHigh())
        .low(quickDto.getRegularMarketDayLow())
        .build();
  }

  public LinePointDTO map(LinePoint point) {
    long unixtimeDatetime  = DateManager.of(Zone.HK).convert(point.getDateTime().toLocalDate()); 
    //from localdatetime to localdate to fill DateManager
    
    return LinePointDTO.builder().dateTime(unixtimeDatetime)
      .close(point.getCloseMKPrice())
      .symbol(point.getSymbol())
      .build();
  }

  public LinePointDTO mapLine(MarketQuickStoreDTO quickDto){
    long unixtimeDatetime =
        DateManager.of(Zone.HK).convert(quickDto.getRegularMarketTime());
    long unixtimeInSeconds = unixtimeDatetime / 1000; 
    Double close = quickDto.getRegularMarketPrice();
    
    return  LinePointDTO.builder().symbol(quickDto.getSymbol())
              .dateTime(unixtimeInSeconds)
              .close(close)
              .build();
  }

  public MarketQuickStoreDTO dtomap(MarketQuickStoreDTO quickDto){
    
    return  MarketQuickStoreDTO.builder().symbol(quickDto.getSymbol())
      .regularMarketPrice(quickDto.getRegularMarketPrice())
      .regularMarketTime(quickDto.getRegularMarketTime())
      .regularMarketOpen(quickDto.getRegularMarketOpen())
      .regularMarketChangePercent(quickDto.getRegularMarketChangePercent())
      .regularMarketDayHigh(quickDto.getRegularMarketDayHigh())
      .regularMarketDayLow(quickDto.getRegularMarketDayLow())
      .ask(quickDto.getAsk()).bid(quickDto.getBid())
      .build();
  } 
}
