package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public record ContentID(Long val) {
  public ContentID {
    if (val < 1) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("content_id_must_be_positive"));
    }
  }

  public static ContentID parse(String x) {
    if (x == null) {
      return null;
    }
    try {
      return new ContentID(Long.valueOf(x));
    } catch (NumberFormatException e) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(rb.getString("invalid_long"), x)
      );
    }
  }
}
