package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfBlank;

import com.acsredux.base.ValidationException;
import java.util.function.IntPredicate;

public record ClearTextPassword(String val) {
  public ClearTextPassword(String val) {
    dieIfBlank(val, "password");
    if (val.length() < 6) {
      throw new ValidationException("password is too short");
    }
    val
      .chars()
      .filter(Character::isUpperCase)
      .findFirst()
      .orElseThrow(() ->
        new ValidationException("password is missing an upper case letter")
      );
    val
      .chars()
      .filter(Character::isDigit)
      .findFirst()
      .orElseThrow(() -> new ValidationException("password is missing a number"));
    IntPredicate isNotDigit = i -> !Character.isDigit(i);
    IntPredicate isNotLetter = i -> !Character.isLetter(i);
    val
      .chars()
      .filter(isNotDigit.and(isNotLetter))
      .findFirst()
      .orElseThrow(() ->
        new ValidationException("password is missing a special character")
      );
    this.val = val;
  }
}
