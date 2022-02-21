package com.acsredux.core.auth.values;

public record User(String name) {
  public static User of(String x) {
    // TODO: validate user string.
    return new User(x);
  }
}
