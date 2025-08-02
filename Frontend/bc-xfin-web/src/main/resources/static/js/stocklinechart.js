document.addEventListener("DOMContentLoaded", function () {
  const stockList = document.getElementById("stock-charts");

  const stockCharts = {};

  const historicalDataBySymbol = {};


  function showEmptyState(container, message = "No data available") {
    container.innerHTML = `<div class="empty-state">${message}</div>`;
  }

  function getfetchLineData() {
    fetch("/v2/chart/stockline?interval=5m")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        /* if (!data || !Array.isArray(data) || data.length === 0) {
          showEmptyState(stockList, "No stock data available");
          return;
        } */

        const stocksBySymbol = groupDataBySymbol(data);

        /* if (Object.keys(stocksBySymbol).length === 0) {
          showEmptyState(stockList, "No valid stock symbols found");
          return;
        } */

        Object.keys(stocksBySymbol).forEach((symbol) => {
          const stockData = stocksBySymbol[symbol];
          createStockChart(symbol, stockData);
        });
      })
      .catch((error) => {
        console.error("Error fetching stock data:", error);
        //showEmptyState(stockList, "Failed to load stock data");
      });
  }

  // 調用函數以獲取股票折線圖數據
  getfetchLineData();

  // 定時更新股票折線圖數據（每10秒更新一次，您可以取消註釋以下代碼）
   setInterval(getfetchLineData, 40000);

   /* function groupDataBySymbol(data) {
    const grouped = {};
    data.forEach((item) => {
      if (!item.symbol) return; // 跳過沒有 symbol 的數據
      if (!grouped[item.symbol]) {
        grouped[item.symbol] = [];
      }
      grouped[item.symbol].push(item);
    });
    return grouped;
  } */

    function groupDataBySymbol(data) {
    data.forEach((item) => {
    const symbol = item.symbol;
    const timestamp = item.dateTime;
    if (!symbol || timestamp == null) return;

    if (!historicalDataBySymbol[symbol]) {
      historicalDataBySymbol[symbol] = new Map(); // 時間去重
    }

    historicalDataBySymbol[symbol].set(timestamp, item);
  });

  const result = {};
  Object.keys(historicalDataBySymbol).forEach(symbol => {
    result[symbol] = Array.from(historicalDataBySymbol[symbol].values());
  });

  return result;
  }

  function createStockChart(symbol, stockData) {
    const formattedData = stockData
      //.filter((item) => item.dateTime && item.close !== null)
      .filter((item) =>
        item.dateTime != null &&
        item.close != null &&
        !isNaN(item.close)
      )
      .map((item) => ({
        time: item.dateTime , 
        value: item.close,
      }))
      .sort((a, b) => a.time - b.time);
      console.log(`Chart Data for ${symbol}:`, formattedData);

  
    if (formattedData.length === 0) {
      showEmptyState(chartContainer, `No data for ${symbol}`);
      return;
    }
  

    if (stockCharts[symbol]) {
       stockCharts[symbol].lineSeries.setData(formattedData);
    return; 
    }
    const chartContainer = document.createElement("div");
    chartContainer.className = "chart-container";
    
    const listItem = document.createElement("li");
    listItem.className = "stock-item";
    listItem.innerHTML = `<h3>${symbol}</h3>`;
    listItem.appendChild(chartContainer);
    
    
    if (stockList) {
      stockList.appendChild(listItem); // 確保 stockList 已定義後再添加 listItem
    } else {
      console.error("stockList is not defined");
      return;
    }
  
    /* const formattedData = stockData
      .filter((item) => item.dateTime && item.close !== null)
      .map((item) => ({
        time: item.dateTime * 1000, 
        value: item.close,
      }))
      .sort((a, b) => a.time - b.time);
  
    if (formattedData.length === 0) {
      showEmptyState(chartContainer, `No data for ${symbol}`);
      return;
    }
   */
    const chart = LightweightCharts.createChart(chartContainer, {
      width: chartContainer.clientWidth,
      //width: 800,
      height: 400,
      layout: {
        background: { type: "solid", color: "#000000" },
        textColor: "#ffffff",
      },
      grid: {
        vertLines: { color: "#1f1f1f" },
        horzLines: { color: "#1f1f1f" },
      },
      /* localization: {  // 新增这部分配置
                    locale: 'zh-CN',  // 设置为中文
                    //dateFormat: 'yyyy年MM月dd日',  // 中文日期格式
                    priceFormatter: price => price.toFixed(2),  // 可选：价格格式化
                    timeFormatter: (timestamp) => {
                    const utc = new Date(timestamp *1000); // timestamp 是秒，*1000 轉成毫秒
                    const hours = utc.getUTCHours().toString().padStart(2, '0');
                    const minutes = utc.getUTCMinutes().toString().padStart(2, '0');
                    return `${hours}:${minutes}`; // 顯示成 16:00
                  },
                    
          }, */
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
        timeVisible: true,
        secondsVisible: false,
        fixLeftEdge: true,
        tickMarkFormatter: (time) => {
        const date = new Date(time );
        return date.toLocaleTimeString('zh-HK', {
          timeZone: 'Asia/Hong_Kong',
          hour: '2-digit',
          minute: '2-digit',
          hour12: false
        });
      }
        
        
      },
    });

    /* function setLocale(locale) {
            chart.applyOptions({
                localization: {
                    locale: locale,
                    dateFormat: 'ja-JP' === locale ? 'yyyy-MM-dd' : "dd MMM 'yy",
                },
            });
        }
        setLocale('ja-JP'); */
  
    const lineSeries = chart.addLineSeries({
      color: "#26a69a",
      lineWidth: 1.5,
    });
  
    lineSeries.setData(formattedData);

    stockCharts[symbol] = { chart, lineSeries };

  
    if (formattedData.length > 0) {
      chart.timeScale().setVisibleRange({
        from: formattedData[0].time,
        to: formattedData[formattedData.length - 1].time,
      });
    }
  }

});




