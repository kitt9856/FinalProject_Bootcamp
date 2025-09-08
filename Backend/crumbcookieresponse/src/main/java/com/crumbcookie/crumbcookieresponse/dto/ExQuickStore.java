package com.crumbcookie.crumbcookieresponse.dto;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExQuickStore {
    private String symbol;
    private LocalTime timestamp;
    private Double open;
    private Double defaulopen;
}
