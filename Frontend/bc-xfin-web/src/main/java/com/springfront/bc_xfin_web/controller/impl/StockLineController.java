package com.springfront.bc_xfin_web.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springfront.bc_xfin_web.controller.StockLineOperation;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.dto.mapper.ChartMapper;
import com.springfront.bc_xfin_web.model.LinePoint;
import com.springfront.bc_xfin_web.model.OHLinePoint;
import com.springfront.bc_xfin_web.service.MarketDataService;

@RestController
@RequestMapping(value = "/v2")
public class StockLineController implements StockLineOperation {

  @Autowired
  private ChartMapper chartMapper;

  @Autowired
  private MarketDataService marketDataService;

  @Override
  public List<LinePointDTO> getLineChart( String interval){
    /* List<LinePoint> pricePoints = switch (LinePoint.TYPE.of(interval)){
      case FIVE_MIN -> getRealTimePointByFiveMinute();
    };
    return pricePoints.stream().map(e -> this.chartMapper.map(e))
      .collect(Collectors.toList()); */
    return this.marketDataService.getLineData();
  }

 
  private List<LinePoint> getRealTimePointByFiveMinute() {
    List<LinePoint> lineChartdata = new ArrayList<>();
    List<MarketQuickStoreDTO> lineDto = this.marketDataService.getmarketquickDTO();
    for (MarketQuickStoreDTO linePointDTO : lineDto) {
      /*  List<MarketQuickStoreDTO> filtered = lineDto.stream()
        .filter(e -> e.getSymbol().equals(linePointDTO.getSymbol()))
        .collect(Collectors.toList());
      if (linePointDTO.getRegularMarketTime().equals(
        filtered.get(filtered.size() -1).getRegularMarketTime()))  {
        continue;
      } else { */
        LinePoint linePoint = new LinePoint(linePointDTO.getRegularMarketTime().getYear(),
      linePointDTO.getRegularMarketTime().getMonthValue(), 
      linePointDTO.getRegularMarketTime().getDayOfMonth(),
      linePointDTO.getRegularMarketTime().getHour(), 
      linePointDTO.getRegularMarketTime().getMinute(), 
      linePointDTO.getRegularMarketPrice());

      linePoint.setSymbol(linePointDTO.getSymbol());
      lineChartdata.add(linePoint);
      
      //}
      
    }
    return lineChartdata;
  }

  @Override
  public List<LinePointDTO> getOHLineChart( String interval){
    List<OHLinePoint> pricePoints = switch (OHLinePoint.TYPE.of(interval)){
      case DAY -> getOHRealTimePointByDay();
    };
    return pricePoints.stream().map(e -> this.chartMapper.map(e))
      .collect(Collectors.toList());
  }

 
  private List<OHLinePoint> getOHRealTimePointByDay() {
    List<OHLinePoint> OHlineChartdata = new ArrayList<>();
    List<MarketQuickStoreDTO> lineDto = this.marketDataService.getOHmarketquickDTO();
    for (MarketQuickStoreDTO linePointDTO : lineDto) {
      OHLinePoint linePoint = new OHLinePoint(linePointDTO.getRegularMarketTime().getYear(),
      linePointDTO.getRegularMarketTime().getMonthValue(), 
      linePointDTO.getRegularMarketTime().getDayOfMonth(),
      linePointDTO.getRegularMarketPrice());

      linePoint.setSymbol(linePointDTO.getSymbol());
      OHlineChartdata.add(linePoint);
    }
    return OHlineChartdata;
  }

 
  
}
