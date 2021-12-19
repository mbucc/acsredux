package com.acsredux.members.values;

import static com.acsredux.base.Util.dieIfBlank;

import com.acsredux.base.ValidationException;
import java.util.ResourceBundle;
import java.util.function.IntPredicate;

public record ClearTextPassword(String val) {
  public ClearTextPassword(String val) {
    var errors = ResourceBundle.getBundle("MemberErrorMessages");
    dieIfBlank(val, errors.getString("password1_missing"));
    if (val.length() < 6) {
      throw new ValidationException(errors.getString("password_too_short"));
    }
    val
      .chars()
      .filter(Character::isUpperCase)
      .findFirst()
      .orElseThrow(() ->
        new ValidationException(errors.getString("password_missing_upper"))
      );
    val
      .chars()
      .filter(Character::isDigit)
      .findFirst()
      .orElseThrow(() ->
        new ValidationException(errors.getString("password_missing_number"))
      );
    IntPredicate isNotDigit = i -> !Character.isDigit(i);
    IntPredicate isNotLetter = i -> !Character.isLetter(i);
    val
      .chars()
      .filter(isNotDigit.and(isNotLetter))
      .findFirst()
      .orElseThrow(() ->
        new ValidationException(errors.getString("password_missing_special"))
      );
    this.val = val;
  }

  @Override
  public String toString() {
    return "*******";
  }
}
