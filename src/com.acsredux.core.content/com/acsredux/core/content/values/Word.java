package com.acsredux.core.content.values;

public record Word(String text) {
  public static Word of(String x) {
    return new Word(x);
  }

  @Override
  public String toString() {
    return text;
  }
}
