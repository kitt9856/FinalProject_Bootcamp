package com.springfront.bc_xfin_web.controller.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.springfront.bc_xfin_web.controller.LineChartOperation;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.mapper.ChartMapper;
import com.springfront.bc_xfin_web.model.LinePoint;

@RestController
@RequestMapping(value = "/v1")
public class LineChartController implements LineChartOperation {
  @Autowired
  private ChartMapper chartMapper;

  @Override
  public List<LinePointDTO> getLineChart( String interval){
    List<LinePoint> pricePoints = switch (LinePoint.TYPE.of(interval)){
      case FIVE_MIN -> getPricePointByFiveMinute();
    };
    return pricePoints.stream().map(e -> this.chartMapper.map(e))
      .collect(Collectors.toList());
  }

  private List<LinePoint> getPricePointByFiveMinute() {
    return Arrays.asList( //
        new LinePoint(2024, 5, 27, 9, 0, 150.0), //
        new LinePoint(2024, 5, 27, 9, 5, 151.0), //
        new LinePoint(2024, 5, 27, 9, 10, 152.0), //
        new LinePoint(2024, 5, 27, 9, 15, 153.0), //
        new LinePoint(2024, 5, 27, 9, 20, 154.0), //
        new LinePoint(2024, 5, 27, 9, 25, 155.0), //
        new LinePoint(2024, 5, 27, 9, 30, 156.0), //
        new LinePoint(2024, 5, 27, 9, 35, 157.0), //
        new LinePoint(2024, 5, 27, 9, 40, 158.0), //
        new LinePoint(2024, 5, 27, 9, 45, 159.0), //
        new LinePoint(2024, 5, 27, 9, 50, 159.0), //
        new LinePoint(2024, 5, 27, 9, 55, 159.0), //
        new LinePoint(2024, 5, 27, 10, 0, 154.0), //
        new LinePoint(2024, 5, 27, 10, 5, 158.0), //
        new LinePoint(2024, 5, 27, 10, 10, 160.0), //
        new LinePoint(2024, 5, 27, 10, 15, 170.0), //
        new LinePoint(2024, 5, 27, 10, 20, 159.0), //
        new LinePoint(2024, 5, 27, 10, 25, 158.0), //
        new LinePoint(2024, 5, 27, 10, 30, 143.0), //
        new LinePoint(2024, 5, 27, 10, 35, 160.0), //
        new LinePoint(2024, 5, 27, 10, 40, 190.0), //
        new LinePoint(2024, 5, 27, 10, 45, 149.0), //
        new LinePoint(2024, 5, 27, 10, 50, 170.0), //
        new LinePoint(2024, 5, 27, 10, 55, 168.0), //
        new LinePoint(2024, 5, 27, 11, 0, 159.0) //
    );
  }
}
