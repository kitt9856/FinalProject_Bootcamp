package com.springfront.bc_xfin_web.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.service.MarketDataService;

@Controller
public class ViewController {

  @Autowired
  private  MarketDataService marketDataService;

  @Autowired
  private LineChartOperation lineChartOperation;

  @Autowired
  private StockLineOperation stockLineOperation;

  @Autowired
  private StockCandleOperation stockCandleOperation;


  /* @GetMapping("/linechart")
  public String linechart(Model model) {
   // List<LinePointDTO> lineCharts = this.marketDataService.getLineData();
   // this.lineChartOperation.getLineChart("5m");
   // model.addAttribute("linechart", lineCharts);
    return "linechart";
  } */

  @GetMapping("/linechart")
    public String linechart(Model model) {
        List<LinePointDTO> lineData = marketDataService.getLineData();
        model.addAttribute("lineData", lineData);
        return "linechart";
    }

  /* @GetMapping("/stocklinechart")
  public String stocklinechart(Model model) {
    return "stocklinechart";
  } */

  @GetMapping("/stocklinechart")
  public String stocklinechart(Model model, @RequestParam(required = false) String symbol) {
    // 獲取所有可用的股票代號
    List<LinePointDTO> allData = stockLineOperation.getLineChart("5m");
    List<String> symbols = allData.stream()
        .map(LinePointDTO::getSymbol)
        .distinct()
        .collect(Collectors.toList());
    
    model.addAttribute("symbols", symbols);
    model.addAttribute("selectedSymbol", symbol);
    
    // 如果有選擇特定股票，過濾數據
    if (symbol != null && !symbol.isEmpty()) {
        List<LinePointDTO> filteredData = allData.stream()
            .filter(d -> d.getSymbol().equals(symbol))
            .collect(Collectors.toList());
        model.addAttribute("lineData", filteredData);
    } else {
        model.addAttribute("lineData", allData);
    }
    
    return "stocklinechart";
}

  @GetMapping("/candlechart")
  public String candlechart(Model model) {
    return "candlechart";
  }

  @GetMapping("/stockcandlechart")
  public String stockcandlechart(Model model,  @RequestParam(required = false) String symbol) {
    List<CandleStickDTO> allData = stockCandleOperation.getCandleChart("1d");
    List<String> symbols = allData.stream()
        .map(CandleStickDTO::getSymbol)
        .distinct()
        .collect(Collectors.toList());
    model.addAttribute("symbols", symbols);
    model.addAttribute("selectedSymbol", symbol);

    if (symbol != null && !symbol.isEmpty()) {
      List<CandleStickDTO> filteredData = allData.stream()
      .filter(d -> d.getSymbol().equals(symbol))
      .collect(Collectors.toList());
      model.addAttribute("candleData", filteredData);
    } else {
      model.addAttribute("candleData", allData);
    }
    return "stockcandlechart";
  }
  
}
