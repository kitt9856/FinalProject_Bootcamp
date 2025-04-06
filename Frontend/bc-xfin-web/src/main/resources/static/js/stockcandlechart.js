document.addEventListener("DOMContentLoaded", function () {
  const stockChartsContainer = document.getElementById("stock-charts");

  const stockCharts = {};

  function showEmptyState(container, message = "No data available") {
    container.innerHTML = `<div class="empty-state">${message}</div>`;
  }

  function getfetchStickData(){
    fetch(`/v2/chart/stockcandle?interval=1d`)
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

  getfetchStickData();

  setInterval(getfetchStickData, 10000);

  function groupBySymbol(data) {
    return data.reduce((acc, item) => {
      if (!acc[item.symbol]) {
        acc[item.symbol] = [];
      }
      acc[item.symbol].push({
        time: Math.floor(Number(item.date)),
        open: item.open,
        high: item.high,
        low: item.low,
        close: item.close,
      });
      return acc;
    }, {});
  }

  function createStockChart(symbol, stockData) {
    if (stockCharts[symbol]) {
      stockCharts[symbol].candleSeries.update({ time: updatedCandleData.time, 
        open: updatedCandleData.open, high: updatedCandleData.high, 
        low: updatedCandleData.low, close: updatedCandleData.close });
     /*  stockCharts[symbol].candleSeries.setData(stockData);
      stockCharts[symbol].chart.timeScale().setVisibleRange({
        from: stockData[0].time,
        to: stockData[stockData.length - 1].time,
      }); */
    } else {
      const listItem = document.createElement("li");
      listItem.className = "stock-chart-item";
      listItem.innerHTML = `<h3>${symbol}</h3><div class="chart-container" id="chart-${symbol}"></div>`;
      stockChartsContainer.appendChild(listItem);

      const chart = LightweightCharts.createChart(
        document.getElementById(`chart-${symbol}`),
        {
          width: 1000,
          height: 600,
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
          },
          timeScale: {
            borderColor: "#7b5353",
            timeVisible: true,
            secondsVisible: false,
            tickMarkFormatter: (time) => {
              const date = new Date(time * 1000);
              const year = date.getFullYear();
              const month = (date.getMonth() + 1).toString().padStart(2, "0");
              const day = date.getDate().toString().padStart(2, "0");
              return `${year}/${month}/${day}`;
            },
          },
        }
      );

      const candleSeries = chart.addCandlestickSeries({
        upColor: "#26a69a",
        downColor: "#ef5350",
        borderUpColor: "#26a69a",
        borderDownColor: "#ef5350",
        wickUpColor: "#26a69a",
        wickDownColor: "#ef5350",
      });

      candleSeries.setData(stockData);

      stockCharts[symbol] = { chart, candleSeries };
    }
  }
});