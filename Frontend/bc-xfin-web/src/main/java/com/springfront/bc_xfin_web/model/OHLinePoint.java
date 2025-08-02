package com.springfront.bc_xfin_web.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OHLinePoint {
    private LocalDate dateTime;
  private Double closeMKPrice;
  private String symbol;

  public OHLinePoint(int year, int month, int dayOfMonth,  Double close) {
    this.dateTime = LocalDate.of(year, month, dayOfMonth);
    this.closeMKPrice = close;
  }

  public enum TYPE {
    DAY,;

    public static OHLinePoint.TYPE of(String type) {
      return switch (type) {
        case "1d" -> OHLinePoint.TYPE.DAY;
        default -> throw new RuntimeException();
      };
    }
  }
  
}
