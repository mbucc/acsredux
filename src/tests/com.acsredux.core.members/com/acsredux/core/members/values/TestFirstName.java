package com.acsredux.core.members.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

class TestFirstName {

  static final ResourceBundle MSGS = ResourceBundle.getBundle("MemberErrorMessages");

  @Test
  void testValidations() {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> new FirstName(null)
    );
    assertEquals(MSGS.getString("firstname_missing"), y.getMessage());
  }

  @Test
  void testTrim() {
    assertEquals("x", (new FirstName(" x ")).val());
  }
}
