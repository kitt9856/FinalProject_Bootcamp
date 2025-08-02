document.addEventListener("DOMContentLoaded", function () {
  const stockList = document.getElementById("stock-charts");
  const stockCharts = {};
  
  // 1. 从<h4 class="chart-title">获取当前股票代码
  function getCurrentSymbol() {
    const titleElement = document.querySelector('.chart-title');
    if (!titleElement) return '0388.HK'; // 默认值
    
    const titleText = titleElement.textContent;
    // 从"股票走势图 - 0388.HK"中提取股票代码
    const symbolMatch = titleText.match(/股票走势图 - (\S+)/);
    return symbolMatch ? symbolMatch[1] : '0388.HK';
  }

  // 2. 清除所有图表
  function clearAllCharts() {
    stockList.innerHTML = '';
    Object.keys(stockCharts).forEach(symbol => {
      if (stockCharts[symbol] && stockCharts[symbol].chart) {
        stockCharts[symbol].chart.remove();
      }
    });
  }

  // 3. 创建单个股票图表
  function createSingleStockChart(symbol, stockData) {
    clearAllCharts();
    
    const chartContainer = document.createElement("div");
    chartContainer.className = "chart-container";
    
    const listItem = document.createElement("li");
    listItem.className = "stock-item";
    listItem.innerHTML = `<h3>${symbol}</h3>`;
    listItem.appendChild(chartContainer);
    stockList.appendChild(listItem);

    const formattedData = stockData
      .filter(item => item.dateTime && item.close !== null)
      .map(item => ({
        time: item.dateTime * 1000,
        value: item.close
      }))
      .sort((a, b) => a.time - b.time);

    const chart = LightweightCharts.createChart(chartContainer, {
      width: chartContainer.clientWidth,
      height: 400,
      layout: { 
        background: { type: "solid", color: "#000000" },
        textColor: "#ffffff"
      },
      grid: {
        vertLines: { color: "#1f1f1f" },
        horzLines: { color: "#1f1f1f" }
      },
      timeScale: {
        timeVisible: true,
        secondsVisible: false
      }
    });

    const lineSeries = chart.addLineSeries({
      color: "#26a69a",
      lineWidth: 1.5
    });
    
    lineSeries.setData(formattedData);
    stockCharts[symbol] = { chart, lineSeries };
  }

  // 4. 主逻辑：根据标题获取股票代码并加载数据
  function loadChartByTitle() {
    const symbol = getCurrentSymbol();
    
    fetch(`/v2/chart/stockline?interval=5m&symbol=${symbol}`)
      .then(response => response.json())
      .then(data => createSingleStockChart(symbol, data))
      .catch(error => {
        console.error("加载图表失败:", error);
        stockList.innerHTML = `<div class="error">无法加载 ${symbol} 的数据</div>`;
      });
  }

  // 5. 初始化加载
  loadChartByTitle();

  // 6. 监听标题变化（适用于AJAX更新场景）
  const titleObserver = new MutationObserver(function(mutations) {
    mutations.forEach(() => loadChartByTitle());
  });
  
  const titleElement = document.querySelector('.chart-title');
  if (titleElement) {
    titleObserver.observe(titleElement, { childList: true, characterData: true });
  }
});