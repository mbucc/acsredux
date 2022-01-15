package com.acsredux.core.members.values;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestZipCode {

  static ResourceBundle msgs = ResourceBundle.getBundle("MemberErrorMessages");

  private static enum BAD_ZIPCODE {
    TOO_SHORT("1234", msgs.getString("zipcode_too_short")),
    NULL(null, msgs.getString("zipcode_missing")),
    NON_DIGITS1("12E45", msgs.getString("zipcode_invalid")),
    NON_DIGITS2("12345-X123", msgs.getString("zipcode_invalid")),
    WRONG_SHAPE1("12345-1", msgs.getString("zipcode_invalid")),
    WRONG_SHAPE2("12345-12", msgs.getString("zipcode_invalid")),
    WRONG_SHAPE3("12345-123", msgs.getString("zipcode_invalid"));

    String val;
    String msg;

    BAD_ZIPCODE(String val, String msg) {
      this.val = val;
      this.msg = msg;
    }
  }

  @ParameterizedTest
  @EnumSource
  void testValidations(BAD_ZIPCODE pwd) {
    ValidationException y = assertThrows(
      ValidationException.class,
      () -> new ZipCode(pwd.val)
    );
    assertEquals(pwd.msg, y.getMessage());
  }

  private static enum GOOD_ZIPCODE {
    FIVE_DIGITS("12345"),
    NINE_DIGITS("12345-1234");

    String val;

    GOOD_ZIPCODE(String val) {
      this.val = val;
    }
  }

  @ParameterizedTest
  @EnumSource
  void testValidations(GOOD_ZIPCODE zip) {
    ZipCode y = assertDoesNotThrow(() -> new ZipCode(zip.val));
    assertEquals(zip.val, y.val());
  }
}
