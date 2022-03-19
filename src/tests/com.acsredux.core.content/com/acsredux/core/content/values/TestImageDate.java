package com.acsredux.core.content.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestImageDate {

  LocalDate x1;
  LocalTime x2;

  @BeforeEach
  void setup() {
    x1 = LocalDate.now();
    x2 = LocalTime.now();
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
    assertEquals(new ImageDate(x1, null), ImageDate.of(fmt(x1)));
  }

  @Test
  void testDateAndTimeString() {
    assertEquals(new ImageDate(x1, x2), ImageDate.of(fmt(x1, x2)));
  }

  @Test
  void testNullRaisesValidationError() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> ImageDate.of(null)
    );
    assertEquals(
      "Please enter an image date like 2022-02-28 or 2022-02-28T12:30.",
      y.getMessage()
    );
  }

  @Test
  void testJustTimeIsAnError() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> ImageDate.of(fmt(x2))
    );
    assertEquals(
      "Please enter an image date like 2022-02-28 or 2022-02-28T12:30.",
      y.getMessage()
    );
  }
}
