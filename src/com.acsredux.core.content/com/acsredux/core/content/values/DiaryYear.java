package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public record DiaryYear(int val) {
  public static DiaryYear parse(String x) {
    if (x == null || x.isBlank()) {
      return null;
    }

    try {
      return new DiaryYear(Integer.parseInt(x.trim()));
    } catch (Exception e) {
      var rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(rb.getString("invalid_year"), x)
      );
    }
  }
}
