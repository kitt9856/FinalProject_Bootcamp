package com.springfront.bc_xfin_web.dto;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CandleStickDTO {
  private String symbol;
  private LocalDate  date;
  private LocalTime time;
  private Long datetime;
  private Double open;
  private Double high;
  private Double low;
  private Double close;
  
}
