

document.addEventListener("DOMContentLoaded", function () {
  const stockList = document.getElementById("stock-charts");
  const stockCharts = {};

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
        const stocksBySymbol = groupDataBySymbol(data);
        Object.keys(stocksBySymbol).forEach((symbol) => {
          const stockData = stocksBySymbol[symbol];
          createStockChart(symbol, stockData);
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

  function createStockChart(symbol, stockData) {
    if (stockCharts[symbol]) {
      stockCharts[symbol].lineSeries.setData(stockData);
      return;
    }

    const listItem = document.createElement("li");
    listItem.className = "stock-item";
    listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="linechart-${symbol}"></div>`;
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
      /* timeScale: {
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
      }, */
      timeScale: {
        timeVisible: true,
        secondsVisible: false,
        fixLeftEdge: true,
        
        
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


