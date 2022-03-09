package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public record PhotoID(Long val) {
  public PhotoID {
    if (val < 1) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("photo_id_must_be_positive"));
    }
  }

  public static PhotoID parse(String x) {
    if (x == null) {
      return null;
    }

    try {
      return new PhotoID(Long.valueOf(x.trim()));
    } catch (NumberFormatException e) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(rb.getString("invalid_photo_id"), x)
      );
    }
  }
}
