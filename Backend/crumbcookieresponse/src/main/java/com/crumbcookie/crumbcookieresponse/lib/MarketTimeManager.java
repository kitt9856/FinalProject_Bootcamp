package com.crumbcookie.crumbcookieresponse.lib;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class MarketTimeManager {

  public static boolean validTradeDay(LocalDate date) {
     // List<DayOfWeek> tradingDates = new ArrayList<>();
      DayOfWeek sourcWeek = date.getDayOfWeek();
      if (! (sourcWeek.equals(DayOfWeek.SATURDAY) || sourcWeek.equals(DayOfWeek.SUNDAY))  ) {
        return true;
      } 
      return false;
  }

  public static boolean isBreakTime(LocalTime time) {
    LocalTime breakstart = LocalTime.of(12, 01);
    LocalTime breakend = LocalTime.of(12, 59);

    if (time.isAfter(breakstart) && time.isBefore(breakend)) {
      return true;
    }
    return false;
  }


  public static LocalDate longToLocalDate(Long marketTime) {
    Instant instant = Instant.ofEpochMilli(marketTime * 1000);
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Hong_Kong"));
    return zonedDateTime.toLocalDate();
  }

  public static LocalDateTime longToLocalDateTime(Long marketTime) {
    Instant instant = Instant.ofEpochMilli(marketTime * 1000);
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Hong_Kong"));
    return zonedDateTime.toLocalDateTime();
  }

  public static String longTOWeek(Long marketTime) {
    // unix use sec as unit, in javaInstant.ofEpochMilli is using mil sec
    Instant instant = Instant.ofEpochMilli(marketTime * 1000); // conv to sec
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Hong_Kong"));
    DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
    String formattedDayOfWeek = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    return formattedDayOfWeek;
  }
  
}
