package com.springfront.bc_xfin_web.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.springfront.bc_xfin_web.controller.StockCandleOperation;
import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.dto.mapper.ChartMapper;
import com.springfront.bc_xfin_web.model.CandleStick;
import com.springfront.bc_xfin_web.service.MarketDataService;

@RestController
@RequestMapping(value = "/v2")
public class StockCandleController implements StockCandleOperation {

  @Autowired
  private ChartMapper chartMapper;

  @Autowired
  private MarketDataService marketDataService;

  @Override
  public List<CandleStickDTO> getCandleChart(String interval) {
    List<CandleStick> candleSticks = switch (CandleStick.TYPE.of(interval)) {
      case DAY -> getCandlesByRealDay();
    };
    return candleSticks.stream().map(e -> this.chartMapper.map(e))
      .collect(Collectors.toList());
  }

  private List<CandleStick> getCandlesByRealDay() {
    List<CandleStick> candleStickdata = new ArrayList<>();
    List<MarketQuickStoreDTO> candleDto = this.marketDataService.getmarketquickDTO();

    for (MarketQuickStoreDTO dto : candleDto) {
      CandleStick candleStick = new CandleStick(dto.getRegularMarketTime().getYear() , 
      dto.getRegularMarketTime().getMonthValue(), 
      dto.getRegularMarketTime().getDayOfMonth(), 
      dto.getRegularMarketOpen(), 
      dto.getRegularMarketDayHigh(), 
      dto.getRegularMarketDayLow(), 
      dto.getRegularMarketPrice());

      candleStick.setSymbol(dto.getSymbol());
      candleStickdata.add(candleStick);
    }

    return candleStickdata;
  }
}
