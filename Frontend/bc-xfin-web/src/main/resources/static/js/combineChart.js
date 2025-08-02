document.addEventListener("DOMContentLoaded", function () {
  const stockList = document.getElementById("stock-charts");
  const btnIntraday = document.getElementById("btn-intraday");
  const btnDaily = document.getElementById("btn-daily");
  
  const stockCharts = {}; // 用於存儲所有圖表實例
  let currentMode = 'intraday'; // 當前顯示模式
  
  // 初始化
  init();
  
  function init() {
    // 綁定按鈕事件
    btnIntraday.addEventListener('click', () => switchMode('intraday'));
    btnDaily.addEventListener('click', () => switchMode('daily'));
    
    // 加載初始數據
    loadData(currentMode);
  }
  
  function switchMode(mode) {
    if (mode === currentMode) return;
    
    currentMode = mode;
    
    // 更新按鈕樣式
    btnIntraday.classList.toggle('active', mode === 'intraday');
    btnDaily.classList.toggle('active', mode === 'daily');
    
    // 清除現有圖表
    clearCharts();
    
    // 加載新數據
    loadData(mode);
  }
  
  function clearCharts() {
    stockList.innerHTML = '';
    Object.keys(stockCharts).forEach(symbol => {
      if (stockCharts[symbol].chart) {
        stockCharts[symbol].chart.remove();
      }
    });
    stockCharts = {};
  }
  
  function loadData(mode) {
    const endpoint = mode === 'intraday' 
      ? "/v2/chart/stockline?interval=5m" 
      : "/v2/chart/OHstockline?interval=1d";
    
    fetch(endpoint)
      .then((response) => {
        if (!response.ok) throw new Error("Network response was not ok");
        return response.json();
      })
      .then((data) => {
        if (!data || !Array.isArray(data)) {
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
          createStockChart(symbol, stockData, mode);
        });
      })
      .catch((error) => {
        console.error("Error fetching stock data:", error);
        showEmptyState(stockList, "Failed to load stock data");
      });
  }
  
  function showEmptyState(container, message = "No data available") {
    container.innerHTML = `<div class="empty-state">${message}</div>`;
  }
  
  function groupDataBySymbol(data) {
    const grouped = {};
    data.forEach((item) => {
      if (!item.symbol) return;
      if (!grouped[item.symbol]) {
        grouped[item.symbol] = [];
      }
      grouped[item.symbol].push(item);
    });
    return grouped;
  }
  
  function createStockChart(symbol, stockData, mode) {
    // 如果圖表已存在，只更新數據
    if (stockCharts[symbol]) {
      const formattedData = formatData(stockData);
      stockCharts[symbol].lineSeries.setData(formattedData);
      return;
    }
    
    const chartContainer = document.createElement("div");
    chartContainer.className = "chart-container";
    
    const listItem = document.createElement("li");
    listItem.className = "stock-item";
    listItem.innerHTML = `<h3>${symbol}</h3>`;
    listItem.appendChild(chartContainer);
    stockList.appendChild(listItem);
    
    const formattedData = formatData(stockData);
    
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
      localization: {
        locale: 'zh-CN',
        dateFormat: 'yyyy年MM月dd日',
        priceFormatter: price => price.toFixed(2)
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: mode === 'intraday', // 當天數據顯示秒數
      },
    });
    
    const lineSeries = chart.addLineSeries({
      color: "#26a69a",
      lineWidth: 1.5,
    });
    
    lineSeries.setData(formattedData);
    
    // 存儲圖表實例
    stockCharts[symbol] = { chart, lineSeries };
    
    // 設置可見範圍
    if (formattedData.length > 0) {
      chart.timeScale().setVisibleRange({
        from: formattedData[0].time,
        to: formattedData[formattedData.length - 1].time,
      });
    }
  }
  
  function formatData(stockData) {
    return stockData
      .filter((item) => item.dateTime && item.close !== null)
      .map((item) => ({
        time: item.dateTime * 1000,
        value: item.close,
      }))
      .sort((a, b) => a.time - b.time);
  }
  
  // 定時更新數據
  setInterval(() => loadData(currentMode), 10000);
});