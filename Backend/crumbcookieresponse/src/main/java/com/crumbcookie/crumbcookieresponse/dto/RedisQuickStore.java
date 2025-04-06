package com.crumbcookie.crumbcookieresponse.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedisQuickStore {
    private String symbol;
    private LocalDateTime regularMarketTime;
    private Double regularMarketPrice;
    private Double regularMarketOpen;
    private Double regularMarketChangePercent;
    private Double regularMarketDayHigh;
    private Double regularMarketDayLow;
    private Double bid;
    private Double ask;
  
}
