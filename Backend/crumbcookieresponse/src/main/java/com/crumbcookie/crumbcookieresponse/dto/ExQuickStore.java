package com.crumbcookie.crumbcookieresponse.dto;

import java.time.LocalTime;
import java.util.LinkedList;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExQuickStore {
    //extra data from CandleStickChart data source(Delay data)
    //using after get YahooStockOpenDTO
    private String symbol;
    private LocalTime timestamp;
    private LinkedList<Double> open;
    private Double defaulopen;
    private LinkedList<Double> high;
    private Double defaulhigh;
    private LinkedList<Double> low;
    private Double defaullow;

}
