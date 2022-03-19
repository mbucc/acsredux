package com.acsredux.adapter.web.photodiary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.adapter.web.common.FormData;
import org.junit.jupiter.api.Test;

public class TestUploadDates {

  @Test
  void testSunnyCase() {
    // setup
    FormData x = new FormData();
    String dt = "2022-02-28T12:30:00";
    x.add("imageDateTime", dt);

    // execute
    UploadHandler.normalizeDates(x);

    // verify
    assertEquals(dt, x.get("imageDate"));
  }

  @Test
  void testDatePickerUsed() {
    // setup
    FormData x = new FormData();
    String dt = "2022-02-28";
    x.add("imageDatePicker", dt);

    // execute
    UploadHandler.normalizeDates(x);

    // verify
    assertEquals(dt, x.get("imageDate"));
  }

  @Test
  void testIfBothLoadedUsePickerValue() {
    // setup
    FormData x = new FormData();
    String dt = "2022-03-28";
    x.add("imageDatePicker", dt);
    x.add("imageDateTime", "2022-02-28T12:30:00");

    // execute
    UploadHandler.normalizeDates(x);

    // verify
    assertEquals(dt, x.get("imageDate"));
  }
}
