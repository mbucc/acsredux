package com.acsredux.core.members.values;

public record EncryptedPassword(String val) {
  public EncryptedPassword(String val) {
    // Any error here is an internal error.
    if (val == null) {
      throw new IllegalStateException("null");
    }
    this.val = val;
  }
}
