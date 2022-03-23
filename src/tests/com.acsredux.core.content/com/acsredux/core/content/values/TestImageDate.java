package com.acsredux.core.content.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestImageDate {

  LocalDate x1;
  LocalTime x2;
  ImageDate expJustDate;
  ImageDate exp;
  ZoneId tz = ZoneId.of("US/Eastern");
  ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");

  @BeforeEach
  void setup() {
    var x = LocalDateTime.now();
    x1 = x.toLocalDate();
    x2 = x.toLocalTime();
    expJustDate = new ImageDate(x1.atStartOfDay().atZone(tz).toInstant());
    exp = new ImageDate(x.atZone(tz).toInstant());
  }

  private String fmt(LocalDate x1) {
    return x1.format(DateTimeFormatter.ISO_LOCAL_DATE);
  }

  private String fmt(LocalTime x1) {
    return x1.format(DateTimeFormatter.ISO_LOCAL_TIME);
  }

  private String fmt(LocalDate x1, LocalTime x2) {
    return (
      x1.format(DateTimeFormatter.ISO_LOCAL_DATE) +
      "T" +
      x2.format(DateTimeFormatter.ISO_LOCAL_TIME)
    );
  }

  @Test
  void testDateStringOnly() {
    assertEquals(expJustDate, ImageDate.of(fmt(x1), tz));
  }

  @Test
  void testDateAndTimeString() {
    assertEquals(exp, ImageDate.of(fmt(x1, x2), tz));
  }

  @Test
  void testNullRaisesValidationError() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> ImageDate.of(null, null)
    );
    assertEquals(rb.getString("invalid_image_date"), y.getMessage());
  }

  @Test
  void testJustTimeIsAnError() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> ImageDate.of(fmt(x2), tz)
    );
    assertEquals(rb.getString("invalid_image_date"), y.getMessage());
  }
}
