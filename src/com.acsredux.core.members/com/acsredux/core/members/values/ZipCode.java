package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;

public record ZipCode(String val) {
  public static final int MIN_LENGTH = 5;
  public static final int MAX_LENGTH = 10;
  public ZipCode(String val) {
    var errors = ResourceBundle.getBundle("MemberErrorMessages");
    dieIfBlank(val, errors.getString("zipcode_missing"));
    if (val.length() < MIN_LENGTH) {
      throw new ValidationException(errors.getString("zipcode_too_short"));
    }
    var xs = val.toCharArray();
    for (int i = 0; i < MIN_LENGTH; i++) {
      if (!Character.isDigit(xs[i])) {
        throw new ValidationException(errors.getString("zipcode_invalid"));
      }
    }
    if (xs.length > 5) {
      if (xs.length != MAX_LENGTH) {
        throw new ValidationException(errors.getString("zipcode_invalid"));
      }
      if (xs[5] != '-') {
        throw new ValidationException(errors.getString("zipcode_invalid"));
      }
      for (int i = 6; i < xs.length; i++) {
        if (!Character.isDigit(xs[i])) {
          throw new ValidationException(errors.getString("zipcode_invalid"));
        }
      }
    }
    this.val = val;
  }
}
