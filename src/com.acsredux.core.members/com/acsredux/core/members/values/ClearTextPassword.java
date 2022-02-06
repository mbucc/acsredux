package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

import com.acsredux.core.base.ValidationException;
import java.util.Arrays;
import java.util.ResourceBundle;

public record ClearTextPassword(char[] val) {
  @Override
  public String toString() {
    return "*******";
  }

  public static ClearTextPassword of(String val) {
    var errors = ResourceBundle.getBundle("MemberErrorMessages");
    dieIfBlank(val, errors.getString("password1_missing"));
    if (val.length() < 6) {
      throw new ValidationException(errors.getString("password_too_short"));
    }
    char[] ys = val.toCharArray();
    boolean hasUpper = false;
    boolean hasLower = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;
    for (char y : ys) {
      hasUpper = hasUpper || Character.isUpperCase(y);
      hasLower = hasLower || Character.isLowerCase(y);
      hasDigit = hasDigit || Character.isDigit(y);
      hasSpecial = hasSpecial || (!Character.isDigit(y) && !Character.isLetter(y));
    }
    if (!hasUpper) {
      throw new ValidationException(errors.getString("password_missing_upper"));
    }
    if (!hasDigit) {
      throw new ValidationException(errors.getString("password_missing_number"));
    }
    if (!hasSpecial) {
      throw new ValidationException(errors.getString("password_missing_special"));
    }
    return new ClearTextPassword(ys);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClearTextPassword that = (ClearTextPassword) o;
    return Arrays.equals(val, that.val);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(val);
  }
}
