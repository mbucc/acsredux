package com.acsredux.core.articles.values;

public record Word(String text) {
  public static Word of(String x) {
    return new Word(x);
  }
}
