package com.springfront.bc_xfin_web.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OHCandleStick {
     private LocalDate date;
  private Double open;
  private Double high;
  private Double low;
  private Double close;
  private String symbol;

  public OHCandleStick (int year, int month, int dayOfMonth, Double open,
  Double high, Double low, Double close) {
    this.date = LocalDate.of(year, month, dayOfMonth);
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
  }

  public enum TYPE {
    DAY;

    public static OHCandleStick.TYPE of(String type) {
      return switch (type) {
        case "1d" -> OHCandleStick.TYPE.DAY;
        default -> throw new RuntimeException();
      };
    }
  }
}
