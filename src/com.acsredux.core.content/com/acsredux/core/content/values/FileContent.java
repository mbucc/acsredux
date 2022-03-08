package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public record FileContent(byte[] val) {
  public static final long MAX_FILE_SIZE_IN_MEGABYTES = 8;
  public static final long MAX_FILE_SIZE_IN_BYTES = MAX_FILE_SIZE_IN_MEGABYTES * 1048576L;

  public FileContent {
    if (val.length == 0) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("file_content_required"));
    }

    if (val.length > MAX_FILE_SIZE_IN_BYTES) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(rb.getString("max_file_size"), MAX_FILE_SIZE_IN_BYTES)
      );
    }
  }
}
