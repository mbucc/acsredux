package com.acsredux.adapter.web.photodiary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.adapter.web.common.FormData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

public class TestUploadDates {

  ZoneId tz = ZoneId.of("US/Pacific");

  @Test
  void testSunnyCase() {
    // setup
    FormData x = new FormData();
    String dt = "2022-02-28T12:30:00";
    x.add("entryDate", dt);

    // execute
    x.normalizeDates(tz);

    // verify
    long expected = LocalDateTime.parse(dt).atZone(tz).toEpochSecond();
    assertEquals(String.valueOf(expected), x.get("entryFromEpochSeconds"));
  }

  @Test
  void testDatePickerUsed() {
    // setup
    FormData x = new FormData();
    String dt = "2022-02-28";
    x.add("imageDatePicker", dt);

    // execute
    x.normalizeDates(tz);

    // verify
    long expected = LocalDate.parse(dt).atStartOfDay().atZone(tz).toEpochSecond();
    assertEquals(String.valueOf(expected), x.get("entryFromEpochSeconds"));
  }

  @Test
  void testIfBothLoadedUsePickerValue() {
    // setup
    FormData x = new FormData();
    String dt = "2022-03-28";
    x.add("imageDatePicker", dt);
    x.add("entryDate", "2022-02-28T12:30:00");

    // execute
    x.normalizeDates(tz);

    // verify
    long expected = LocalDate.parse(dt).atStartOfDay().atZone(tz).toEpochSecond();
    assertEquals(String.valueOf(expected), x.get("entryFromEpochSeconds"));
  }
}
