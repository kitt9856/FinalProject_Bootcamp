package com.springfront.bc_xfin_web.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MarketQuickStoreDTO {
    private String symbol;
    private LocalDateTime regularMarketTime;
    private Double regularMarketPrice;
    private Double regularMarketChangePercent;
    private Double regularMarketOpen;
    private Double regularMarketDayHigh;
    private Double regularMarketDayLow;
    private Double bid;
    private Double ask;
}
