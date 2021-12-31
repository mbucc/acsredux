package com.acsredux.core.members.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestClearTextPassword {

  static ResourceBundle msgs = ResourceBundle.getBundle("MemberErrorMessages");

  private static enum BAD_PASSWORD {
    MISSING_UPPERCASE("abcd3f!", msgs.getString("password_missing_upper")),
    TOO_SHORT("12345", msgs.getString("password_too_short")),
    MISSING_SPECIAL_CHAR("aBcD3F", msgs.getString("password_missing_special")),
    NO_NUMBER("abc#eFg", msgs.getString("password_missing_number"));

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
