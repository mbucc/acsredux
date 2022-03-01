package com.acsredux.core.base;

import java.io.IOException;
import java.io.InputStream;

public class Util {

  private Util() {
    throw new UnsupportedOperationException("static only");
  }

  public static void dieIfBlank(String x, String msg) {
    if (x == null || x.isBlank()) {
      throw new ValidationException(msg);
    }
  }

  public static void req(Object x, String msg) {
    if (x == null) {
      throw new ValidationException(msg);
    }
  }

  public static void die(Object x, String msg) {
    if (x == null) {
      throw new IllegalStateException(msg);
    }
  }

  public static String readResource(String x) {
    String prefix = "resource '" + x + "'";
    InputStream y = Thread.currentThread().getContextClassLoader().getResourceAsStream(x);
    try (y) {
      if (y == null) {
        String cwd = System.getProperty("user.dir");
        String cp = System.getProperty("java.class.path");
        String msg = String.format(
          "%s not found, with cwd=%s and classpath=\n%s",
          prefix,
          cwd,
          String.join("\n", cp.split("[;:]"))
        );
        throw new IllegalStateException(msg);
      }
      return new String(y.readAllBytes());
    } catch (IOException e) {
      throw new IllegalStateException("error reading " + prefix, e);
    }
  }
}
