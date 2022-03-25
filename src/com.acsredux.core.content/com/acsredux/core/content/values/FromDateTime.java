package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record FromDateTime(Instant val) {
  public FromDateTime {
    die(val, "null val");
  }

  public Month getMonthValue(ZoneId tz) {
    return val.atZone(tz).toLocalDate().getMonth();
  }
  public String asString(ZoneId tz) {
    return val.atZone(tz).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
  }
}
