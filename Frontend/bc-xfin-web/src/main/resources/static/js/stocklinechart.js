document.addEventListener("DOMContentLoaded", function () {
  const stockList = document.getElementById("stock-charts");

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
        if (!data || !Array.isArray(data) || data.length === 0) {
          showEmptyState(stockList, "No stock data available");
          return;
        }

        const stocksBySymbol = groupDataBySymbol(data);

        if (Object.keys(stocksBySymbol).length === 0) {
          showEmptyState(stockList, "No valid stock symbols found");
          return;
        }

        Object.keys(stocksBySymbol).forEach((symbol) => {
          const stockData = stocksBySymbol[symbol];
          createStockChart(symbol, stockData);
        });
      })
      .catch((error) => {
        console.error("Error fetching stock data:", error);
        showEmptyState(stockList, "Failed to load stock data");
      });
  }

  // 調用函數以獲取股票折線圖數據
  getfetchLineData();

  // 定時更新股票折線圖數據（每10秒更新一次，您可以取消註釋以下代碼）
   setInterval(getfetchLineData, 10000);

   function groupDataBySymbol(data) {
    const grouped = {};
    data.forEach((item) => {
      if (!item.symbol) return; // 跳過沒有 symbol 的數據
      if (!grouped[item.symbol]) {
        grouped[item.symbol] = [];
      }
      grouped[item.symbol].push(item);
    });
    return grouped;
  }

  function createStockChart(symbol, stockData) {
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
  
    const formattedData = stockData
      .filter((item) => item.dateTime && item.close !== null)
      .map((item) => ({
        time: item.dateTime, 
        value: item.close,
      }))
      .sort((a, b) => a.time - b.time);
  
    if (formattedData.length === 0) {
      showEmptyState(chartContainer, `No data for ${symbol}`);
      return;
    }
  
    const chart = LightweightCharts.createChart(chartContainer, {
      width: 1000,
      height: 400,
      layout: {
        background: { type: "solid", color: "#000000" },
        textColor: "#ffffff",
      },
      grid: {
        vertLines: { color: "#1f1f1f" },
        horzLines: { color: "#1f1f1f" },
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: false,
        
        
      },
    });
  
    const lineSeries = chart.addLineSeries({
      color: "#26a69a",
      lineWidth: 1.5,
    });
  
    lineSeries.setData(formattedData);
  
    if (formattedData.length > 0) {
      chart.timeScale().setVisibleRange({
        from: formattedData[0].time,
        to: formattedData[formattedData.length - 1].time,
      });
    }
  }

});


