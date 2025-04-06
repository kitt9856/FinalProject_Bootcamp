package com.springfront.bc_xfin_web.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.springfront.bc_xfin_web.dto.CandleStickDTO;

public interface StockCandleOperation {
  
  @GetMapping(value = "/chart/stockcandle")
  List<CandleStickDTO> getCandleChart(@RequestParam String interval);

}
