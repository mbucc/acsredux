package com.acsredux.core.members.values;

import java.time.Instant;

public record RegistrationDate(Instant val) {
  public RegistrationDate {
    // Any error here is an internal error.
    if (val == null) {
      throw new IllegalStateException("null");
    }
  }
}
