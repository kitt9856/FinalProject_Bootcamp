document.addEventListener("DOMContentLoaded", function () {
  const stockChartsContainer = document.getElementById("OHstock-candlecharts");

  const stockCharts = {};

  function showEmptyState(container, message = "No data available") {
    container.innerHTML = `<div class="empty-state">${message}</div>`;
  }

  function getfetchStickData(){
    fetch(`/v2/chart/OHstockcandle?interval=1d`)
    .then((response) => response.json())
    .then((data) => {
      const stocksBySymbol = groupBySymbol(data);

      Object.entries(stocksBySymbol).forEach(([symbol, stockData]) => {
        createOHStockChart(symbol, stockData);
      });
    })
    .catch((error) => {
      console.error("Error fetching candlestick data:", error);
    });
  }

  getfetchStickData();

  setInterval(getfetchStickData, 10000);

  function groupBySymbol(data) {
    return data.reduce((acc, item) => {
      if (!acc[item.symbol]) {
        acc[item.symbol] = [];
      }
      acc[item.symbol].push({
        time: toLocalTimestamp(item.date), //Math.floor(Number(item.date) ),
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
//     
//    } else {
//      const listItem = document.createElement("li");
//      listItem.className = "stock-chart-item";
//      listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="OHchart-${symbol}"></div>`;
//      stockChartsContainer.appendChild(listItem);
//
//      const chart = LightweightCharts.createChart(
//        document.getElementById(`OHchart-${symbol}`),
//        {
//          width: 800,
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
//              //return `${year}/${month}/${day}`;
//              return `${day}/${month}/${year}`;
//              /* const date = new Date(time * 1000);
//              return formatChineseDate(date, true); */
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
//
//      /* function formatChineseDate(date, showFullYear = false) {
//    const year = showFullYear 
//      ? date.getFullYear() + '年' 
//      : (date.getFullYear() - 2000) + '年';
//    const month = (date.getMonth() + 1) + '月';
//    const day = date.getDate() + '日';
//    
//    return `${year}${month}${day}`; // 輸出格式：25年2月20日
//  } */
//
//    
//
//
//
//    
//    
//
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

function createOHStockChart(symbol, stockData) {
  if (stockCharts[symbol]) {
    stockCharts[symbol].candleSeries.update(stockData[stockData.length - 1]);
    return;
  }

  const listItem = document.createElement("li");
  listItem.className = "stock-chart-item";
  listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="OHchart-${symbol}"></div>`;
  stockChartsContainer.appendChild(listItem);

  const container = document.getElementById(`OHchart-${symbol}`);
  
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
    localization: {  // 新增这部分配置
                   locale: 'zh-CN',  // 设置为中文
                   dateFormat: 'yyyy年MM月dd日',  // 中文日期格式
                   priceFormatter: price => price.toFixed(2)  // 可选：价格格式化
         },
    timeScale: {
      borderColor: "#7b5353",
      timeVisible: false, // 改為true顯示時間
      secondsVisible: false,
      rightOffset: 10, // 右側留白
      barSpacing: 8, // 柱子間距
      minBarSpacing: 4, // 最小柱子間距
     // fixLeftEdge: true,
      fixRightEdge: true,
      //lockVisibleTimeRangeOnResize: true,
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

  

  const candleSeries = chart.addCandlestickSeries({
    upColor: "#26a69a",
    downColor: "#ef5350",
    borderUpColor: "#26a69a",
    borderDownColor: "#ef5350",
    wickUpColor: "#26a69a",
    wickDownColor: "#ef5350",
    priceScaleId: 'right',
  });

  candleSeries.setData(stockData);
  
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

