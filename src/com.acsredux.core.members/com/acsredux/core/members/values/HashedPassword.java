package com.acsredux.core.members.values;

public record HashedPassword(String val) {
  public HashedPassword {
    // Any error here is an internal error.
    if (val == null) {
      throw new IllegalStateException("null");
    }
  }
}
