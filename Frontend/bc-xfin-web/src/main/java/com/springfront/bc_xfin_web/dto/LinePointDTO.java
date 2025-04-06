package com.springfront.bc_xfin_web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LinePointDTO {
  private String symbol;
  private Long dateTime;
  private Double close;
}
