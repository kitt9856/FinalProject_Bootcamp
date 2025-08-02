package com.springfront.bc_xfin_web.controller.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springfront.bc_xfin_web.controller.CandleChartOperation;
import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.mapper.ChartMapper;
import com.springfront.bc_xfin_web.model.CandleStick;

@RestController
@RequestMapping(value = "/v1")
public class CandleChartController implements CandleChartOperation{
  
  @Autowired
  private ChartMapper chartMapper;

  @Override
  public List<CandleStickDTO> getCandleChart(String interval) {
    List<CandleStick> candleSticks = switch (CandleStick.TYPE.of(interval)) {
      case DAY -> getCandlesByDay();
    };
    return candleSticks.stream().map(e -> this.chartMapper.map(e))
      .collect(Collectors.toList());
  }

  private List<CandleStick> getCandlesByDay() {
    return Arrays.asList( //
        new CandleStick(2024, 1, 1, 9, 0, 100.0, 105.0, 98.0, 102.0), // 2024-01-01
        new CandleStick(2024, 1, 2,9, 0, 102.0, 106.0, 101.0, 105.0), // 2024-01-02
        new CandleStick(2024, 1, 3,9, 0, 103.0, 107.0, 102.0, 104.0), // 2024-01-03
        new CandleStick(2024, 1, 4,9, 0, 104.0, 108.0, 103.0, 107.0), // 2024-01-04
        new CandleStick(2024, 1, 5,9, 0, 107.0, 109.0, 105.0, 106.0), // 2024-01-05
        new CandleStick(2024, 1, 6,9, 0, 108.0, 110.0, 107.0, 109.0), // 2024-01-06
        new CandleStick(2024, 1, 7,9, 0, 109.0, 112.0, 108.0, 111.0), // 2024-01-07
        new CandleStick(2024, 1, 8,9, 0, 111.0, 119.0, 108.0, 108.0), // 2024-01-08
        new CandleStick(2024, 1, 9,9, 0, 108.0, 110.0, 105.0, 105.0), // 2024-01-09
        new CandleStick(2024, 1, 10,9, 0, 105.0, 111.0, 104.0, 111.0) // 2024-01-10
    );
  }
  
}
