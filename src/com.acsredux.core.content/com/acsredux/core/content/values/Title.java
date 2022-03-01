package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;

public record Title(String val) {
  public Title {
    if (val.isBlank()) {
      throw new ValidationException("Title cannot be empty");
    }
  }
  public static Title of(String x) {
    return x == null ? null : new Title(x);
  }
}
