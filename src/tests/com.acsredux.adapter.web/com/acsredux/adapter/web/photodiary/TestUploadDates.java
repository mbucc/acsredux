package com.acsredux.adapter.web.photodiary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.adapter.web.common.FormData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TestUploadDates {

  ZoneId tz = ZoneId.of("US/Pacific");


  @Test
  void testSunnyCase() {
    // setup
    FormData x = new FormData();
    String dt = "2022-02-28T12:30:00";
    x.add("imageDateTime", dt);

    // execute
    UploadHandler.normalizeDates(x, tz);

    // verify
    long expected = LocalDateTime.parse(dt).atZone(tz).toEpochSecond();
    assertEquals(String.valueOf(expected), x.get("imageDate"));
  }

  @Test
  void testDatePickerUsed() {
    // setup
    FormData x = new FormData();
    String dt = "2022-02-28";
    x.add("imageDatePicker", dt);

    // execute
    UploadHandler.normalizeDates(x, tz);

    // verify
    long expected = LocalDate.parse(dt).atStartOfDay().atZone(tz).toEpochSecond();
    assertEquals(String.valueOf(expected), x.get("imageDate"));
  }

  @Test
  void testIfBothLoadedUsePickerValue() {
    // setup
    FormData x = new FormData();
    String dt = "2022-03-28";
    x.add("imageDatePicker", dt);
    x.add("imageDateTime", "2022-02-28T12:30:00");

    // execute
    UploadHandler.normalizeDates(x, tz);

    // verify
    long expected = LocalDate.parse(dt).atStartOfDay().atZone(tz).toEpochSecond();
    assertEquals(String.valueOf(expected), x.get("imageDate"));
  }
}
