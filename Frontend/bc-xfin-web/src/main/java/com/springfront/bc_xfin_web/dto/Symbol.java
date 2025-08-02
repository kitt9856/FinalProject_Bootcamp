package com.springfront.bc_xfin_web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Symbol {
  private Long id;
  private String symbol;  
  private String longName;
    
}
