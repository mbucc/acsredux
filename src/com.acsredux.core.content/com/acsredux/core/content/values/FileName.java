package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public record FileName(String val) {
  public static final int MAX_FILENAME_LENGTH = 100;

  public FileName {
    if (val == null || val.isBlank()) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("filename_required"));
    }

    if (val.length() > MAX_FILENAME_LENGTH) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(
          rb.getString("max_upload_filename_length"),
          MAX_FILENAME_LENGTH
        )
      );
    }
  }

  /**
   * Return a new FileName with the given suffix inserted before the extension.
   */
  public FileName insertSuffix(String x) {
    if (x == null) {
      throw new IllegalArgumentException("null suffix");
    }
    String y1 = this.val();
    int p = y1.lastIndexOf('.');
    if (p == -1) {
      return new FileName(x + "." + y1);
    } else {
      return new FileName(y1.substring(0, p) + "." + x + y1.substring(p));
    }
  }
}
