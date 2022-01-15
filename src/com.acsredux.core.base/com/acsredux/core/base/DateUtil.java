package com.acsredux.core.base;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtil {

  private DateUtil() {
    throw new UnsupportedOperationException("static only");
  }

  // Uses local set in the environment, typically via the LC_ALL envvar on Linux.
  public static String fullDate(Instant x, ZoneId tz) {
    return x.atZone(tz).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
  }
}
