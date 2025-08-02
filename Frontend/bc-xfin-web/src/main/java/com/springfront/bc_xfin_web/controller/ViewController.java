package com.springfront.bc_xfin_web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springfront.bc_xfin_web.dto.CandleStickDTO;
import com.springfront.bc_xfin_web.dto.LinePointDTO;
import com.springfront.bc_xfin_web.dto.MarketQuickStoreDTO;
import com.springfront.bc_xfin_web.lib.Mathoperation;
import com.springfront.bc_xfin_web.service.MarketDataService;

import jakarta.servlet.http.HttpSession;

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

  
  

 /*  @Autowired
  private MarketQuickStoreDTO marketQuickStoreDTO; */


  /* @GetMapping("/linechart")
  public String linechart(Model model) {
   // List<LinePointDTO> lineCharts = this.marketDataService.getLineData();
   // this.lineChartOperation.getLineChart("5m");
   // model.addAttribute("linechart", lineCharts);
    return "linechart";
  } */

  @GetMapping(value = "/getCallData")
  public List<MarketQuickStoreDTO> getQuickData() {
    return this.marketDataService.getmarketquickDTO();
  }

  @GetMapping(value = "/getOHCallData")
  public List<MarketQuickStoreDTO> getOHQuickData() {
    return this.marketDataService.getOHmarketquickDTO();
  }



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
  @GetMapping("/newstocklinechart")
  public String nstocklinechart(Model model, @RequestParam(required = false) String symbol) {
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
    
    return "newstocklinechart";
}

@GetMapping("/OHstocklinechart")
  public String OHstocklinechart(Model model, @RequestParam(required = false) String symbol) {
    // 獲取所有可用的股票代號
    List<LinePointDTO> allData = stockLineOperation.getOHLineChart("1d");
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
    
    return "OHstocklinechart";
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

 

  @GetMapping("/OHstockcandlechart")
  public String OHstockcandlechart(Model model,  @RequestParam(required = false) String symbol) {
    List<CandleStickDTO> allData = stockCandleOperation.getOHCandleChart("1d");
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
    return "OHstockcandlechart";
  }

  /* @GetMapping("/homepage")
public String stockPriceChecker(Model model, @RequestParam(required = false) String keyword, @RequestParam(required = false) Boolean showSuggestions) {
  List<MarketQuickStoreDTO> list = new ArrayList<>();

    if(keyword != null && !keyword.isEmpty()) {
        list = marketDataService.getByKeyword(keyword);
        
        // 如果是為了顯示建議，不處理完整搜索
        if(showSuggestions != null && showSuggestions) {
            model.addAttribute("suggestions", list);
            return "fragments/search-suggestions :: suggestionsFragment";
        }
        
        if (!list.isEmpty()) {
            model.addAttribute("DTO", list.get(0).getSymbol()); 
        } else {
            for (MarketQuickStoreDTO dto : list) {
                model.addAttribute("DTO", dto.getSymbol());
            }
        }
    } else {
        list = marketDataService.getmarketquickDTO();
    }
    
    model.addAttribute("list", list);
    model.addAttribute("keyword", keyword);
    /* // 獲取所有可用的股票代號
    List<LinePointDTO> allData = stockLineOperation.getOHLineChart("1d");
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
        model.addAttribute("stockData", filteredData);
    } */
    
    /* return "homepage"; // 對應到您的 index.html 檔案
}  */

  @RequestMapping(path = {"/", "/search"})
  public String home(MarketQuickStoreDTO marketQuickStoreDTO, Model model, 
    String keyword,@RequestParam(required = false) Boolean showCandlestick, HttpSession session) {


    //List<MarketQuickStoreDTO> list = new ArrayList<>();
    List<MarketQuickStoreDTO> list = marketDataService.getmarketquickDTO();
    String defaultSymbol = "0388.HK";
    boolean pricedecrease = false;

    List<String> symbols = list.stream()
        .map(MarketQuickStoreDTO::getSymbol)
        .distinct()
        .collect(Collectors.toList());
    //model.addAttribute("symbols", symbols);
    Map<String,String> AllsymbolMap = this.marketDataService.getAllSymbolsName();
    model.addAttribute("symbols", AllsymbolMap);
    Map<String,String> defaultSymbolMap = new HashMap<>();
    defaultSymbolMap.put(defaultSymbol, AllsymbolMap.get(defaultSymbol));
    Map<String,String> symbolsMap = new HashMap<>();

    



    


    MarketQuickStoreDTO defaultStock = list.stream()
        .filter(dto -> defaultSymbol.equals(dto.getSymbol()))
        .findFirst()
        .orElse(null);

    /* Boolean showCandle = (showCandlestick != null) ? showCandlestick : 
                        (Boolean) session.getAttribute("showCandlestick");
    if (showCandle == null) {
        showCandle = false; // 默认显示折线图
        session.setAttribute("showCandlestick", showCandle);
    } */
      
    if(defaultStock != null){
      
      model.addAttribute("DTO",list.get(0).getSymbol() );
      model.addAttribute("LongName",list.get(0).getSymbolFullName() );

      model.addAttribute("currentPrice", Mathoperation.roundPrice(list.get(0).getRegularMarketPrice()));
      model.addAttribute("currentChange",  Mathoperation.roundPrice(list.get(0).getRegularMarketChangePercent()) );
      if (list.get(0).getRegularMarketChangePercent() > 0) {
           pricedecrease = true;
         } else if (list.get(0).getRegularMarketChangePercent() < 0) {
           pricedecrease = false;
         } 
      model.addAttribute("pricedecrease", list.get(0).getRegularMarketChangePercent() != 0 ? pricedecrease : null);
    }


    if(keyword != null &&  !keyword.isEmpty() ) {
       list = marketDataService.getByKeyword(keyword);
       if (!list.isEmpty()) {
         //model.addAttribute("DTO", list.get(0).getSymbol()); 
         //symbolsMap.clear();
         //symbolsMap.put(list.get(0).getSymbol(), symbols.get(list.get(0).getSymbol()));
         //model.addAttribute("DTO", list.get(0).getSymbol());
         model.addAttribute("DTO", list.get(0).getSymbol()); 
         model.addAttribute("LongName",list.get(0).getSymbolFullName() );

         model.addAttribute("currentPrice", Mathoperation.roundPrice(list.get(0).getRegularMarketPrice()));

         if (list.get(0).getRegularMarketChangePercent() > 0) {
           pricedecrease = true;
         } else if (list.get(0).getRegularMarketChangePercent() < 0) {
           pricedecrease = false;
         } 
         model.addAttribute("currentChange",  Mathoperation.roundPrice(list.get(0).getRegularMarketChangePercent()) );
         model.addAttribute("pricedecrease", list.get(0).getRegularMarketChangePercent() != 0 ? pricedecrease : null);

         //model.addAttribute("list", list); 
       }else{
         /* for (MarketQuickStoreDTO dto : list) {
        model.addAttribute("DTO", dto.getSymbol());
      } */
        list = marketDataService.getmarketquickDTO();
        /*  List<String> uniqueSymbols = list.stream()
        .map(MarketQuickStoreDTO::getSymbol)
        .distinct()
        .collect(Collectors.toList());
        model.addAttribute("DTO", uniqueSymbols); */

        
       } 
      // model.addAttribute("list", list);
      /*  for (MarketQuickStoreDTO dto : list) {
        if (dto.getSymbol().equals(keyword)) {
          model.addAttribute("DTO", dto.getSymbol());
          break;
        } 
       }  */
   /*  else {
       list = marketDataService.getmarketquickDTO(); 
      
      /* for (MarketQuickStoreDTO dto : list) {
        model.addAttribute("DTO", dto.getSymbol());
      } */
   } 
    //model.addAttribute("DTO", defaultSymbol);

    list = marketDataService.getmarketquickDTO();
    /* List<String> uniqueSymbols = list.stream()
        .map(MarketQuickStoreDTO::getSymbol)
        .distinct()
        .collect(Collectors.toList());
        model.addAttribute("ALLDTO", uniqueSymbols); */
    model.addAttribute("list", list);
    model.addAttribute("DedaulfPrice", Mathoperation.roundPrice(list.get(0).getRegularMarketPrice()));
   // model.addAttribute("showCandlestick", showCandlestick != null && showCandlestick);
    return "homepage";
  }  

  /* @PostMapping("/toggle-chart-type")
public String toggleChartType(@RequestParam boolean showCandlestick, HttpSession session) {
    session.setAttribute("showCandlestick", showCandlestick);
    return "/search"; // 或其他适当的返回路径
} */

 
  

  /* @GetMapping("/homepage")
public String search(
    @RequestParam String keyword,
    @RequestParam(required = false) Boolean showSuggestions,
    Model model) {
    
    List<MarketQuickStoreDTO> results = marketDataService.getByKeyword(keyword);
    
    if (showSuggestions != null && showSuggestions) {
        // 只返回建议列表HTML片段
        model.addAttribute("suggestions", results);
        return "suggestions :: suggestions";
    }
    
    // 正常返回完整页面
    model.addAttribute("list", results);
    if (!results.isEmpty()) {
        model.addAttribute("DTO", results.get(0).getSymbol());
    }
    return "homepage";
} */

  

  
}
