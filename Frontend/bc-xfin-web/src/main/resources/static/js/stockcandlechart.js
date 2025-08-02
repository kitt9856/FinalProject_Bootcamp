document.addEventListener("DOMContentLoaded", function () {
  const stockChartsContainer = document.getElementById("stock-candlecharts");

  const stockCharts = {};
  const seriesMap = {};

  function showEmptyState(container, message = "No data available") {
    container.innerHTML = `<div class="empty-state">${message}</div>`;
  }

  function refreshChartData() {
   fetch(`/v2/chart/stockcandle?interval=5m`)
    .then(response => response.json())
    .then(data => {
      const stocksBySymbol = groupBySymbol(data);

      Object.entries(stocksBySymbol).forEach(([symbol, stockData]) => {
        createStockChart(symbol, stockData); // 內部已能檢查是否已有圖表
        //updateOrCreateChart(symbol, stockData);
      });
    })
    .catch(error => {
      console.error("Error updating candle chart data:", error);
    });
}

// 每30秒刷新一次
setInterval(refreshChartData, 30000);
refreshChartData();


  function getfetchStickData(){
    fetch(`/v2/chart/stockcandle?interval=5m`)
    .then((response) => response.json())
    .then((data) => {
      const stocksBySymbol = groupBySymbol(data);

      Object.entries(stocksBySymbol).forEach(([symbol, stockData]) => {
        createStockChart(symbol, stockData);
      });
    })
    .catch((error) => {
      console.error("Error fetching candlestick data:", error);
    });
  }

  //getfetchStickData();

  //setInterval(getfetchStickData, 10000);
  //setInterval(getfetchStickData, 10000);

  function groupBySymbol(data) {
    return data.reduce((acc, item) => {
      /* if (!acc[item.symbol]) {
        acc[item.symbol] = [];
      } */
      const isValidCandle = 
        item.symbol &&
        item.datetime != null && !isNaN(item.datetime) &&
        item.open != null && !isNaN(item.open) &&
        item.high != null && !isNaN(item.high) &&
        item.low != null && !isNaN(item.low) &&
        item.close != null && !isNaN(item.close);
      
      if (!isValidCandle) {
      //console.warn(" 無效 candle 被過濾:", item);
      return acc;
    }
      if (!acc[item.symbol]) {
      acc[item.symbol] = [];
    }
      acc[item.symbol].push({
        //time: Math.floor(Number(item.date)),
        time: item.datetime * 1000 ,
        open: item.open,
        high: item.high,
        low: item.low,
        close: item.close,
      });
      return acc;
    }, {});
  }

  function toLocalTimestamp(apiDateString) {
  const date = new Date(apiDateString);
  return Math.floor(date.getTime() / 1000); // 轉換為秒級時間戳
}


//  function createStockChart(symbol, stockData) {
//    if (stockCharts[symbol]) {
//      stockCharts[symbol].candleSeries.update({ time: updatedCandleData.time, 
//        open: updatedCandleData.open, high: updatedCandleData.high, 
//        low: updatedCandleData.low, close: updatedCandleData.close });
//     /*  stockCharts[symbol].candleSeries.setData(stockData);
//      stockCharts[symbol].chart.timeScale().setVisibleRange({
//        from: stockData[0].time,
//        to: stockData[stockData.length - 1].time,
//      }); */
//    } else {
//      const listItem = document.createElement("li");
//      listItem.className = "stock-chart-item";
//      listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="chart-${symbol}"></div>`;
//      stockChartsContainer.appendChild(listItem);
//
//      const chart = LightweightCharts.createChart(
//        document.getElementById(`chart-${symbol}`),
//        {
//          width: 600,
//          height: 400,
//          layout: {
//            background: { type: "solid", color: "#000000" },
//            textColor: "#ffffff",
//          },
//          grid: {
//            vertLines: { color: "#1f1f1f" },
//            horzLines: { color: "#1f1f1f" },
//          },
//          priceScale: {
//            borderColor: "#7b5353",
//          },
//          localization: {  // 新增这部分配置
//                    locale: 'zh-CN',  // 设置为中文
//                    dateFormat: 'yyyy年MM月dd日',  // 中文日期格式
//                    priceFormatter: price => price.toFixed(2)  // 可选：价格格式化
//          },
//          timeScale: {
//            borderColor: "#7b5353",
//            timeVisible: false, 
//            secondsVisible: false,
//            tickMarkFormatter: (time) => {
//              const date = new Date(time * 1000);
//              const year = date.getFullYear();
//              const month = (date.getMonth() + 1).toString().padStart(2, "0");
//              const day = date.getDate().toString().padStart(2, "0");
//              return `${day}/${month}/${year}`;
//            },
//          },
//        }
//      );
//      function setLocale(locale) {
//            chart.applyOptions({
//                localization: {
//                    locale: locale,
//                    dateFormat: 'ja-JP' === locale ? 'yyyy-MM-dd' : "dd MMM 'yy",
//                },
//            });
//        }
//        setLocale('ja-JP'); 

function createStockChart(symbol, stockData) {
  /* if (stockCharts[symbol]) {
  // 如果資料數量多於 1，則完整 setData()，避免 update 時出錯
    if (stockData.length > 1) {
      stockCharts[symbol].candleSeries.setData(stockData);
    } else if (stockData.length === 1) {
      stockCharts[symbol].candleSeries.update(stockData[0]);
    }
    return;
  } */
  const sortedData = stockData.sort((a, b) => a.time - b.time);
  console.log(`Chart Data for ${symbol}:`, sortedData);

    if (stockCharts[symbol]) {
      const { chart, candleSeries } = stockCharts[symbol];
      if (sortedData.length > 1) {
        candleSeries.setData(sortedData);
      } else if (sortedData.length === 1) {
        candleSeries.update(sortedData[0]);
      }
      chart.timeScale().fitContent();
      return;
    }
  

  const listItem = document.createElement("li");
  listItem.className = "stock-chart-item";
  listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="chart-${symbol}"></div>`;
  stockChartsContainer.appendChild(listItem);

  const container = document.getElementById(`chart-${symbol}`);
  
  const chart = LightweightCharts.createChart(container, {
    // 使用容器寬度而非固定值
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
      scaleMargins: { // 調整價格刻度邊距
        top: 0.1,
        bottom: 0.2,
      },
    },
    //localization: {  // 新增这部分配置
    //               locale: 'zh-CN',  // 设置为中文
    //               dateFormat: 'yyyy年MM月dd日',  // 中文日期格式
    //               priceFormatter: price => price.toFixed(2)  // 可选：价格格式化
    //     },
    localization: {
      locale: 'zh-CN',  // 设置为中文
                    //dateFormat: 'yyyy年MM月dd日',  // 中文日期格式
                    //priceFormatter: price => price.toFixed(2),  // 可选：价格格式化
                    timeFormatter: (timestamp) => {
                    const utc = new Date(timestamp  ); // timestamp 是秒，*1000 轉成毫秒
                    return utc.toLocaleTimeString('zh-HK', {
                      timeZone: 'Asia/Hong_Kong',
                      hour: '2-digit',
                      minute: '2-digit',
                      hour12: false
                    });
                  },
      priceFormatter: price => price.toFixed(2)
    },

    timeScale: {
      borderColor: "#7b5353",
      timeVisible: true, // 改為true顯示時間
      secondsVisible: false,
      rightOffset: 10, // 右側留白
      barSpacing: 8, // 柱子間距
      minBarSpacing: 4, // 最小柱子間距
      /* fixLeftEdge: true,
      fixRightEdge: true,
      lockVisibleTimeRangeOnResize: true, */
      tickMarkFormatter: (time) => {
        const date = new Date(time );
        return date.toLocaleString('zh-CN', {
          timeZone: 'Asia/Hong_Kong',
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        });
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

  

  

  const candleSeries = chart.addCandlestickSeries({
    upColor: "#26a69a",
    downColor: "#ef5350",
    borderUpColor: "#26a69a",
    borderDownColor: "#ef5350",
    wickUpColor: "#26a69a",
    wickDownColor: "#ef5350",
    priceScaleId: 'right',
  });

  //candleSeries.setData(stockData);
  /* candleSeries.setData(
  stockData.sort((a, b) => a.time - b.time)
); */
  candleSeries.setData(sortedData);

  
  // 自動調整視圖範圍
  chart.timeScale().fitContent();
  
  // 添加窗口大小改變監聽
  const resizeObserver = new ResizeObserver(() => {
    chart.applyOptions({ width: container.clientWidth });
    chart.timeScale().fitContent();
  });
  resizeObserver.observe(container);
  
  stockCharts[symbol] = { chart, candleSeries, resizeObserver };
}
});



//      const candleSeries = chart.addCandlestickSeries({
//        upColor: "#26a69a",
//        downColor: "#ef5350",
//        borderUpColor: "#26a69a",
//        borderDownColor: "#ef5350",
//        wickUpColor: "#26a69a",
//        wickDownColor: "#ef5350",
//      });
//
//      candleSeries.setData(stockData);
//
//      stockCharts[symbol] = { chart, candleSeries };
//    }
//  }
//}); 