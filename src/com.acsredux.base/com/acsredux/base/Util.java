package com.acsredux.base;

public class Util {

  private Util() {
    throw new UnsupportedOperationException("static only");
  }

  public static void dieIfBlank(String x, String name) {
    if (x == null || x.isBlank()) {
      throw new ValidationException(name + " is required");
    }
  }

  public static void dieIfNull(Object x, String name) {
    if (x == null) {
      throw new ValidationException(name + " is required");
    }
  }
}
