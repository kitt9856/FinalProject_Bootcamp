document.addEventListener("DOMContentLoaded", function () {
  const chart = LightweightCharts.createChart(
    document.getElementById("chart-container"),
    {
      width: 1000,
      height: 600,
      layout: {
        background: { type: "solid", color: "#000000" }, // Force black background
        textColor: "#ffffff", // White text for contrast
      },
      grid: {
        vertLines: { color: "#1f1f1f" }, // Dark grid
        horzLines: { color: "#1f1f1f" },
      },
      priceScale: {
        borderColor: "#7b5353",
      },
      timeScale: {
        borderColor: "#7b5353",
        timeVisible: true, // Enable time visibility
        secondsVisible: false,
        tickMarkFormatter: (time) => {
          const date = new Date(time * 1000); // Convert Unix timestamp to milliseconds
          const hours = date.getHours().toString().padStart(2, "0");
          const minutes = date.getMinutes().toString().padStart(2, "0");
          return `${hours}:${minutes}`; // Show time in HH:mm format
        },
      },
    }
  );

  // Add line series instead of candlestick series
  const lineSeries = chart.addLineSeries({
    color: "#26a69a", // Green line color
    lineWidth: 1.5, // Line thickness
  });

  chart.applyOptions({
    timeScale: {
      barSpacing: 20, // Adjust the spacing if necessary
    },
  });

  const params = new URLSearchParams({
    interval: "5m", // 5min data
  });

  fetch(`/v1/chart/line?${params.toString()}`)
    .then((response) => response.json())
    .then((data) => {
      const stockData = data
        .filter(
          (item) =>
            item.dateTime &&
            item.close !== null // Only need close price for line chart
        )
        .map((item) => ({
          time: Math.floor(Number(item.dateTime)), // Unix timestamp
          value: item.close, // Line chart uses value (close) data
        }))
        .sort((a, b) => a.time - b.time);
      // Set the data for the line series
      console.log(stockData);
      lineSeries.setData(stockData);

      chart.timeScale().setVisibleRange({
        from: stockData[0].time, // Start from the first data point
        to: stockData[stockData.length - 1].time, // End at the last data point
      });
    })
    .catch((error) => {
      console.error("Error fetching line chart data:", error);
    });
});
