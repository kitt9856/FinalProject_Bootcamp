package com.springfront.bc_xfin_web.dto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CandleStickDTO {
  private String symbol;
  private Long date;
  private Double open;
  private Double high;
  private Double low;
  private Double close;
  
}
