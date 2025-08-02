package com.springfront.bc_xfin_web.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinePoint {
  private LocalDateTime dateTime;
  private Double closeMKPrice;
  private String symbol;

  public LinePoint(int year, int month, int dayOfMonth, int hour, int minute,
  Double close) {
    this.dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    this.closeMKPrice = close;
  }

  public enum TYPE {
    FIVE_MIN,;

    public static LinePoint.TYPE of(String type) {
      return switch (type) {
        case "5m" -> LinePoint.TYPE.FIVE_MIN;
        default -> throw new RuntimeException();
      };
    }
  }
  
}
