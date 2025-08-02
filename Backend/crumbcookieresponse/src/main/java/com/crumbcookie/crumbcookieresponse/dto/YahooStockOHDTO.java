package com.crumbcookie.crumbcookieresponse.dto;

import java.util.List;

import com.crumbcookie.crumbcookieresponse.dto.YahooStockOHDTO.Chart.OHError;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockOHDTO.Chart.OHResult.Indicators;
import com.crumbcookie.crumbcookieresponse.dto.YahooStockOHDTO.Chart.OHResult.Meta.CurrentTradingPeriod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class YahooStockOHDTO {

    private   Chart chart;

    @Getter
    @AllArgsConstructor
    public static  class Chart {

        private List<OHResult> result;
        private OHError error;

        @Getter
        @AllArgsConstructor
        public static class OHResult {

            private Meta meta;
            private List<Long> timestamp;
            private Indicators indicators;

            @Getter
            @Builder
            @AllArgsConstructor
            public static class Meta {

                private String currency;
                private String symbol;
                private String exchangeName;
                private String fullExchangeName;
                private String instrumentType;
                private Long firstTradeDate;
                private Long regularMarketTime;
                private Boolean hasPrePostMarketData;
                private int gmtoffset;  //second time unit
                private String timezone;
                private String exchangeTimezoneName;
                private Double regularMarketPrice;
                private Double fiftyTwoWeekHigh;
                private Double fiftyTwoWeekLow;
                private Double regularMarketDayHigh;
                private Double regularMarketDayLow;
                private Long regularMarketVolume;
                private String longName;
                private String shortName;
                private Double chartPreviousClose;
                private int priceHint;
                private CurrentTradingPeriod currentTradingPeriod;
                private String dataGranularity;
                private String range;
                private List<String> validRanges;

                @Getter
                @Builder
                @AllArgsConstructor
                public static class CurrentTradingPeriod {

                    private TradingPeriod pre;
                    private TradingPeriod regular;
                    private TradingPeriod post;

                    @Getter
                    @Builder
                    @AllArgsConstructor
                    public static class TradingPeriod {

                        private String timezone;
                        private Long start;
                        private Long end;
                        private int gmtoffset;

                    }

                }

            }

            @Getter
            @Builder
            @AllArgsConstructor
            public static class Indicators {

                private List<Quote> quote;
                private List<AdjClose> adjclose;

                @Getter
                @Builder
                @AllArgsConstructor
                public static class Quote {

                    private List<Double> high;
                    private List<Double> close;
                    private List<Double> open;
                    private List<Double> low;
                    private List<Long> volume;
                }

                /* @Getter
    @Builder
    @AllArgsConstructor */
                public static class AdjClose {

                    private List<Double> adjclose;
                }
            }

        }

        @Getter
        @Builder
        @AllArgsConstructor
        public static class OHError {

            private String code;
            private String message;
        }
    }
}
