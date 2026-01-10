package com.crumbcookie.crumbcookieresponse.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YahooStockOpenDTO {

    private Chart chart;

    // getter, setter
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Chart {
        //delay dataDTO for produce candlestick chart

        private List<Result> result;
        private QuoteOpenError error;

        // getter, setter
        @Getter
        @Builder
        @AllArgsConstructor
        public static class Result {

            private Meta meta;
            private List<Long> timestamp;
            private Indicators indicators;

            // getter, setter
            @Getter
            @Builder
            @AllArgsConstructor
            public static class Meta {

                private String currency;
                private String symbol;
                private String exchangeName;
                private String fullExchangeName;
                private String instrumentType;
                private long firstTradeDate;
                private long regularMarketTime;
                private boolean hasPrePostMarketData;
                private int gmtoffset;
                private String timezone;
                private String exchangeTimezoneName;
                private double regularMarketPrice;
                private double fiftyTwoWeekHigh;
                private double fiftyTwoWeekLow;
                private double regularMarketDayHigh;
                private double regularMarketDayLow;
                private long regularMarketVolume;
                private String longName;
                private String shortName;
                private double chartPreviousClose;
                private double previousClose;
                private int scale;
                private int priceHint;
                //private CurrentTradingPeriod currentTradingPeriod;
                //private List<List<TradingPeriod>> tradingPeriods;
                private String dataGranularity;
                private String range;
                private List<String> validRanges;

                // getter, setter

                /* public static class CurrentTradingPeriod {
                    private Period pre;
                    private Period regular;
                    private Period post;

                    // getter, setter

                    public static class Period {
                        private String timezone;
                        private long start;
                        private long end;
                        private int gmtoffset;

                        // getter, setter
                    }
                } */

 /* public static class TradingPeriod {
                    private String timezone;
                    private long start;
                    private long end;
                    private int gmtoffset;

                    // getter, setter
                } */
            }

            @Getter
            @Builder
            @AllArgsConstructor
            public static class Indicators {

                
                private List<Quote> quote;

                @Getter
                @Builder
                @AllArgsConstructor
                public static class Quote {

                    private List<Double> high;
                    private List<Double> low;
                    private List<Double> close;
                    //private List<Long> volume;
                    private List<Double> open;

                }
            }
        }

        @Getter
        public static class QuoteOpenError {

            private String code;
            private String description;
        }
    }
}
