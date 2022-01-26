package com.acsredux.core.members.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

class TestLastName {

  static ResourceBundle msgs = ResourceBundle.getBundle("MemberErrorMessages");

  @Test
  void testNullRaisesException() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> new LastName(null)
    );
    assertEquals(msgs.getString("lastname_missing"), y.getMessage());
  }

  @Test
  void testEmptyRaisesException() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> new LastName("  ")
    );
    assertEquals(msgs.getString("lastname_missing"), y.getMessage());
  }

  @Test
  void testTrim() {
    assertEquals("x", (new LastName(" x ")).val());
  }
}
