package com.acsredux.core.members.values;

import static org.junit.jupiter.api.Assertions.*;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestClearTextPassword {

  static final ResourceBundle MSGS = ResourceBundle.getBundle("MemberErrorMessages");

  private enum BAD_PASSWORD {
    MISSING_UPPERCASE("abcd3f!", MSGS.getString("password_missing_upper")),
    TOO_SHORT("12345", MSGS.getString("password_too_short")),
    MISSING_SPECIAL_CHAR("aBcD3F", MSGS.getString("password_missing_special")),
    NO_NUMBER("abc#eFg", MSGS.getString("password_missing_number"));

    final String val;
    final String msg;

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
      () -> ClearTextPassword.of(pwd.val)
    );
    assertEquals(pwd.msg, y.getMessage());
  }

  @Test
  void testEquality() {
    // setup
    var x1 = new ClearTextPassword("abc".toCharArray());
    var x2 = new ClearTextPassword("abc".toCharArray());
    assertEquals(x1, x2);
  }
}
