package com.springfront.bc_xfin_web.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandleStick {
  private LocalDate date;
  private LocalDateTime dateTime;
  private Double open;
  private Double high;
  private Double low;
  private Double close;
  private String symbol;

  public CandleStick (int year, int month, int dayOfMonth, int hour, int minute, Double open,
  Double high, Double low, Double close) {
    this.date = LocalDate.of(year, month, dayOfMonth);
    this.dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
  }

  public enum TYPE {
    DAY;

    public static CandleStick.TYPE of(String type) {
      return switch (type) {
        case "1d" -> CandleStick.TYPE.DAY;
        default -> throw new RuntimeException();
      };
    }
  }

}
