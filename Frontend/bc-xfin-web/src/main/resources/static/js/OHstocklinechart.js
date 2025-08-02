//document.addEventListener("DOMContentLoaded", function () {
//  const stockList = document.getElementById("OHstock-charts");
//
//  const stockCharts = {};
//
//  function showEmptyState(container, message = "No data available") {
//    container.innerHTML = `<div class="empty-state">${message}</div>`;
//  }
//
//  function getfetchLineData() {
//    fetch("/v2/chart/OHstockline?interval=1d")
//      .then((response) => {
//        if (!response.ok) {
//          throw new Error("Network response was not ok");
//        }
//        return response.json();
//      })
//      .then((data) => {
//        /* if (!data || !Array.isArray(data) || data.length === 0) {
//          showEmptyState(stockList, "No   stock data available");
//          return;
//        } */
//
//        const stocksBySymbol = groupDataBySymbol(data);
//
//        /* if (Object.keys(stocksBySymbol).length === 0) {
//          showEmptyState(stockList, "No valid stock symbols found");
//          return;
//        } */
//
//        Object.keys(stocksBySymbol).forEach((symbol) => {
//          const stockData = stocksBySymbol[symbol];
//          createOHStockChart(symbol, stockData);
//        });
//      })
//      .catch((error) => {
//        console.error("Error fetching stock data:", error);
//        //showEmptyState(stockList, "Failed to load stock data");
//      });
//  }
//
//  // 調用函數以獲取股票折線圖數據
//  getfetchLineData();
//
//  // 定時更新股票折線圖數據（每10秒更新一次，您可以取消註釋以下代碼）
//   setInterval(getfetchLineData, 10000);
//
//   
//
//   function groupDataBySymbol(data) {
//    const grouped = {};
//    data.forEach((item) => {
//      if (!item.symbol) return; // 跳過沒有 symbol 的數據
//      if (!grouped[item.symbol]) {
//        grouped[item.symbol] = [];
//      }
//      grouped[item.symbol].push(item);
//    });
//    return grouped;
//  }
//
//  
//
//  
//
//  function createOHStockChart(symbol, stockData) {
//
//    function getContainerWidth(container) {
//  // 強制觸發瀏覽器重排 (reflow) 以確保能獲取正確寬度
//  container.style.display = 'inline-block';
//  const width = container.clientWidth;
//  container.style.display = ''; // 恢復原始 display 屬性
//  return width;
//}
//    
//    if (stockCharts[symbol]) {
//       stockCharts[symbol].lineSeries.setData(formattedData);
//    return; 
//    }
//    const chartContainer = document.createElement("div");
//    chartContainer.className = "chart-container";
//    
//    const listItem = document.createElement("li");
//    listItem.className = "stock-item";
//    listItem.innerHTML = `<h3>${symbol}</h3>`;
//    listItem.appendChild(chartContainer);
//    
//    if (stockList) {
//      stockList.appendChild(listItem); // 確保 stockList 已定義後再添加 listItem
//    } else {
//      console.error("stockList is not defined");
//      return;
//    }
//
//    const containerWidth = getContainerWidth(chartContainer);
//
//    
//  
//    const formattedData = stockData
//      .filter((item) => item.dateTime && item.close !== null)
//      .map((item) => ({
//        time: item.dateTime * 1000, 
//        value: item.close,
//      }))
//      .sort((a, b) => a.time - b.time);
//  
//    if (formattedData.length === 0) {
//      showEmptyState(chartContainer, `No data for ${symbol}`);
//      return;
//    }
//  
//    const chart = LightweightCharts.createChart(chartContainer, {
//      //width: 800,
//      width: containerWidth,
//      height: 400,
//      layout: {
//        background: { type: "solid", color: "#000000" },
//        textColor: "#ffffff",
//      },
//      grid: {
//        vertLines: { color: "#1f1f1f" },
//        horzLines: { color: "#1f1f1f" },
//      },
//      localization: {  // 新增这部分配置
//                    locale: 'zh-CN',  // 设置为中文
//                    dateFormat: 'yyyy年MM月dd日',  // 中文日期格式
//                    priceFormatter: price => price.toFixed(2)  // 可选：价格格式化
//          },
//      timeScale: {
//        timeVisible: true,
//        secondsVisible: false,
//        
//        
//      },
//    });
//
//    function setLocale(locale) {
//            chart.applyOptions({
//                localization: {
//                    locale: locale,
//                    dateFormat: 'ja-JP' === locale ? 'yyyy-MM-dd' : "dd MMM 'yy",
//                },
//            });
//        }
//        setLocale('ja-JP');
//  
//    const lineSeries = chart.addLineSeries({
//      color: "#26a69a",
//      lineWidth: 1.5,
//    });
//  
//    lineSeries.setData(formattedData);
//
//    stockCharts[symbol] = { chart, lineSeries };
//  
//    if (formattedData.length > 0) {
//      chart.timeScale().setVisibleRange({
//        from: formattedData[0].time,
//        to: formattedData[formattedData.length - 1].time,
//      });
//    }
//  }
//
//});

document.addEventListener("DOMContentLoaded", function () {
  const stockList = document.getElementById("OHstock-charts");
  const stockCharts = {};

  function showEmptyState(container, message = "No data available") {
    container.innerHTML = `<div class="empty-state">${message}</div>`;
  }

  function getfetchLineData() {
    fetch("/v2/chart/OHstockline?interval=1d")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        const stocksBySymbol = groupDataBySymbol(data);
        Object.keys(stocksBySymbol).forEach((symbol) => {
          const stockData = stocksBySymbol[symbol];
          createOHStockChart(symbol, stockData);
        });
      })
      .catch((error) => {
        console.error("Error fetching stock data:", error);
      });
  }

  getfetchLineData();
  setInterval(getfetchLineData, 10000);

  function groupDataBySymbol(data) {
    const grouped = {};
    data.forEach((item) => {
      if (!item.symbol) return;
      if (!grouped[item.symbol]) {
        grouped[item.symbol] = [];
      }
      grouped[item.symbol].push({
        time: toLocalTimestamp(item.dateTime),
        value: item.close,
      });
    });
    return grouped;
  }

  function toLocalTimestamp(apiDateString) {
    const date = new Date(apiDateString * 1000);
    return Math.floor(date.getTime() );
  }

  function createOHStockChart(symbol, stockData) {
    if (stockCharts[symbol]) {
      stockCharts[symbol].lineSeries.setData(stockData);
      return;
    }

    const listItem = document.createElement("li");
    listItem.className = "stock-item";
    listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="OHlinechart-${symbol}"></div>`;
    stockList.appendChild(listItem);

    const container = document.getElementById(`OHlinechart-${symbol}`);
    
    const chart = LightweightCharts.createChart(container, {
      width: container.clientWidth,
      height: 400,
      layout: {
        background: { type: "solid", color: "#000000" },
        textColor: "#ffffff",
      },
      grid: {
        vertLines: { color: "#1f1f1f" },
        horzLines: { color: "#1f1f1f" },
      },
      priceScale: {
        borderColor: "#7b5353",
        scaleMargins: {
          top: 0.1,
          bottom: 0.2,
        },
      },
      localization: {
        locale: 'zh-CN',
        dateFormat: 'yyyy年MM月dd日',
        priceFormatter: price => price.toFixed(2)
      },
      timeScale: {
        borderColor: "#7b5353",
        timeVisible: false,
        secondsVisible: false,
        rightOffset: 10,
        barSpacing: 8,
        minBarSpacing: 4,
        fixRightEdge: true,
        tickMarkFormatter: (time) => {
          const date = new Date(time * 1000);
          const year = date.getFullYear();
          const month = (date.getMonth() + 1).toString().padStart(2, "0");
          const day = date.getDate().toString().padStart(2, "0");
          return `${day}/${month}/${year}`;
        },
      },
      handleScroll: {
        mouseWheel: true,
        pressedMouseMove: true,
        horzTouchDrag: true,
        vertTouchDrag: false
      },
      handleScale: {
        axisPressedMouseMove: true,
        mouseWheel: true,
        pinch: true
      }
    });

    const lineSeries = chart.addLineSeries({
      color: "#26a69a",
      lineWidth: 1.5,
      priceScaleId: 'right',
    });

    lineSeries.setData(stockData);
    chart.timeScale().fitContent();
    
    const resizeObserver = new ResizeObserver(() => {
      chart.applyOptions({ width: container.clientWidth });
      chart.timeScale().fitContent();
    });
    resizeObserver.observe(container);
    
    stockCharts[symbol] = { chart, lineSeries, resizeObserver };
  }
});


