package com.springfront.bc_xfin_web.lib;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;


public class DateManager {
  
  private ZoneId zoneId;

  private DateManager(Zone zone) {
    this.zoneId = switch (zone) {
      case HK -> ZoneId.of("Asia/Hong_Kong");
    };
  }

  public static DateManager of(Zone zone) {
    return new DateManager(zone);
  }

  public long convert(LocalDate date) {
    return date.atStartOfDay(this.zoneId).toEpochSecond();
  }

  public long convert(LocalDateTime dateTime) {
    return dateTime.atZone(this.zoneId).toEpochSecond();
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



  public static enum Zone {
    HK,;
  }
  
}
