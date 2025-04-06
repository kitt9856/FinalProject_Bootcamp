package com.springfront.bc_xfin_web.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.springfront.bc_xfin_web.dto.LinePointDTO;

public interface StockLineOperation {
  
   @GetMapping(value = "/chart/stockline")
  List<LinePointDTO> getLineChart(@RequestParam String interval);
  
}
