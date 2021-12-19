package com.acsredux.base;

public class Util {

  private Util() {
    throw new UnsupportedOperationException("static only");
  }

  public static void dieIfBlank(String x, String msg) {
    if (x == null || x.isBlank()) {
      throw new ValidationException(msg);
    }
  }

  public static void dieIfNull(Object x, String msg) {
    if (x == null) {
      throw new ValidationException(msg);
    }
  }
}
