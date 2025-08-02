package com.crumbcookie.crumbcookieresponse.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedisQuickStore {
    private String symbol;
    private String symbolFullName;
    private LocalDateTime regularMarketTime;
    private Double regularMarketPrice;
    private Double regularMarketOpen;
    private Double regularMarketChangePercent;
    private Double regularMarketDayHigh;
    private Double regularMarketDayLow;
    private Double bid;
    private Double ask;
  
}
