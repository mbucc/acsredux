package com.acsredux.base.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.base.ValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ClearTextPasswordTest {

  private static enum BAD_PASSWORD {
    MISSING_UPPERCASE("abcd3f!", "password is missing an upper case letter"),
    TOO_SHORT("12345", "password is too short"),
    MISSING_SPECIAL_CHAR("aBcD3F", "password is missing a special character"),
    NO_NUMBER("abc#eFg", "password is missing a number");

    String val;
    String msg;

    BAD_PASSWORD(String val, String msg) {
      this.val = val;
      this.msg = msg;
    }
  }

  @ParameterizedTest
  @EnumSource
  void testValidations(BAD_PASSWORD pwd) {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> new ClearTextPassword(pwd.val)
    );
    assertEquals(pwd.msg, y.getMessage());
  }
}
