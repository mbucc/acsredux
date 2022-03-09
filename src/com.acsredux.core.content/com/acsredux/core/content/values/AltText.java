package com.acsredux.core.content.values;

public record AltText(String val) {
  public static AltText of(FileName x) {
    if (x == null || x.val() == null) {
      return null;
    }
    String y = x.val();
    int i = y.lastIndexOf('.');
    if (i == -1) {
      return new AltText(y);
    }
    return new AltText(y.substring(0, i));
  }
}
