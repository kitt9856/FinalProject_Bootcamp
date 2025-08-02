package com.springfront.bc_xfin_web.dto.mapper;

import org.springframework.stereotype.Component;

import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.lib.DateManager;
import com.springfront.bc_xfin_web.lib.DateManager.Zone;
import com.springfront.bc_xfin_web.model.CandleStick;
import com.springfront.bc_xfin_web.model.LinePoint;
import com.springfront.bc_xfin_web.model.OHCandleStick;
import com.springfront.bc_xfin_web.model.OHLinePoint;

@Component
public class ChartMapper {
  
  public CandleStickDTO map(CandleStick candleStick) {
    long unixtimeDate  = DateManager.of(Zone.HK).convert(candleStick.getDateTime());
    return CandleStickDTO.builder().date(candleStick.getDate())
      .time(DateManager.longToLocalTime(unixtimeDate))
      .datetime(unixtimeDate / 1000)
      .open(candleStick.getOpen()).close(candleStick.getClose())
      .high(candleStick.getHigh()).low(candleStick.getLow())
      .symbol(candleStick.getSymbol())
      .build();
  }

  public CandleStickDTO map(OHCandleStick OHcandleStick) {
    long unixtimeDate  = DateManager.of(Zone.HK).convert(OHcandleStick.getDate());
    return CandleStickDTO.builder().date(OHcandleStick.getDate())
      .time(DateManager.longToLocalTime(unixtimeDate))
      .open(OHcandleStick.getOpen()).close(OHcandleStick.getClose())
      .high(OHcandleStick.getHigh()).low(OHcandleStick.getLow())
      .symbol(OHcandleStick.getSymbol())
      .build();
  }

  public CandleStickDTO mapCandle(MarketQuickStoreDTO quickDto) {
    long unixtimeDatetime =
        DateManager.of(Zone.HK).convert(quickDto.getRegularMarketTime());
    long epochMilli = DateManager.of(Zone.HK).convertToMilli(quickDto.getRegularMarketTime());
    return CandleStickDTO.builder().symbol(quickDto.getSymbol())
        .date(DateManager.longToLocalDate(unixtimeDatetime))
        .time(DateManager.longToLocalTime(epochMilli))
        .datetime(epochMilli /1000)
        .open(quickDto.getRegularMarketOpen())
        .close(quickDto.getRegularMarketPrice())
        .high(quickDto.getRegularMarketDayHigh())
        .low(quickDto.getRegularMarketDayLow())
        .build();
  }

 

  public LinePointDTO map(LinePoint point) {
    long unixtimeDatetime  = DateManager.of(Zone.HK).convert(point.getDateTime().toLocalDate()); 
    //from localdatetime to localdate to fill DateManager
    long unixtimeInSeconds = unixtimeDatetime / 1000; 

    
    return LinePointDTO.builder().dateTime(unixtimeInSeconds)
      .close(point.getCloseMKPrice())
      .symbol(point.getSymbol())
      .build();
  }

  public LinePointDTO map(OHLinePoint point) {
    long unixtimeDatetime  = DateManager.of(Zone.HK).convert(point.getDateTime()); 
    long unixtimeInSeconds = unixtimeDatetime / 1000; 

    //from localdatetime to localdate to fill DateManager
    
    return LinePointDTO.builder().dateTime(unixtimeInSeconds)
      .close(point.getCloseMKPrice())
      .symbol(point.getSymbol())
      .build();
  }

  public LinePointDTO mapLine(MarketQuickStoreDTO quickDto){
    long unixtimeDatetime =
        DateManager.of(Zone.HK).convert(quickDto.getRegularMarketTime());
    long unixtimeInSeconds = unixtimeDatetime / 1000; 
    long epochMilli = DateManager.of(Zone.HK).convertToMilli(quickDto.getRegularMarketTime());

    Double close = quickDto.getRegularMarketPrice();
    
    return  LinePointDTO.builder().symbol(quickDto.getSymbol())
              .dateTime(epochMilli)
              .close(close)
              .build();
  }

  public MarketQuickStoreDTO dtomap(MarketQuickStoreDTO quickDto){
    
    return  MarketQuickStoreDTO.builder().symbol(quickDto.getSymbol())
      .symbolFullName(quickDto.getSymbolFullName())
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
