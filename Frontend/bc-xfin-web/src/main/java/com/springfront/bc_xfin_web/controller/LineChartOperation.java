package com.springfront.bc_xfin_web.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.springfront.bc_xfin_web.dto.LinePointDTO;

public interface LineChartOperation {

  @GetMapping(value = "/chart/line")
  List<LinePointDTO> getLineChart(@RequestParam String interval);
  
}
