package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;
import static java.lang.Math.min;

import com.acsredux.core.base.ValidationException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

public record BlobBytes(byte[] val) {
  public static final long MAX_FILE_SIZE_IN_MEGABYTES = 8;
  public static final long MAX_FILE_SIZE_IN_BYTES = MAX_FILE_SIZE_IN_MEGABYTES * 1048576L;

  public BlobBytes {
    if (val == null) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("file_content_required"));
    }
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlobBytes blobBytes = (BlobBytes) o;
    return Arrays.equals(val, blobBytes.val);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(val);
  }

  @Override
  public String toString() {
    int n = min(25, val.length);
    String maybeDots = n < val.length ? ", ... ]" : "]";
    String y = Arrays.toString(Arrays.copyOfRange(val, 0, n));
    return "BlobBytes{val=" + y.substring(0, y.length() - 1) + maybeDots + '}';
  }

  public String asString() {
    return new String(val, StandardCharsets.UTF_8);
  }

  public static BlobBytes ofString(String x) {
    die(x, "null string");
    return new BlobBytes(x.getBytes(StandardCharsets.UTF_8));
  }
}
