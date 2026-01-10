package com.crumbcookie.crumbcookieresponse.lib;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class DateTimeManager {
  private ZoneId zoneId;

  private DateTimeManager(Zone zone) {
    this.zoneId = switch (zone) {
      case HK -> ZoneId.of("Asia/Hong_Kong");
    };
  }

  public static DateTimeManager of(Zone zone) {
    return new DateTimeManager(zone);
  }

  public DateTimeManager() {
    this.zoneId = ZoneId.of("Asia/Hong_Kong"); 
}

  public long convert(LocalDate date) {
    return date.atStartOfDay(this.zoneId).toEpochSecond();
  }

  public long convert(LocalDateTime dateTime) {
    return dateTime.atZone(this.zoneId).toEpochSecond();
  }

  public long convert(int year, int month, int dayOfMonth, int hour, int minute, int second) {
    LocalDateTime dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
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

  public static LocalTime longToLocalTime(Long marketTime) {
    Instant instant = Instant.ofEpochMilli(marketTime * 1000);
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Hong_Kong"));
    return zonedDateTime.toLocalTime();
  }



  public static enum Zone {
    HK,;
  }

 /*  public static void main(String[] args) {
    Long lo = 1739151L;
    
    System.out.println(DateTimeManager.longToLocalDateTime(lo  * 1000));
  } */
  
}
