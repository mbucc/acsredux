package com.acsredux.core.articles.values;

import com.acsredux.core.base.ValidationException;

public record DiaryYear(int val) {
  public static DiaryYear parse(String x) {
    if (x.isBlank()) {
      return null;
    }
    try {
      return new DiaryYear(Integer.parseInt(x));
    } catch (Exception e) {
      throw new ValidationException("invalid year '" + x + "'");
    }
  }
}
