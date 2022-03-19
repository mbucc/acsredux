package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;

public enum ImageOrientation {
  PORTRAIT,
  LANDSCAPE;

  public static ImageOrientation of(String x) {
    ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
    if (x == null) {
      throw new ValidationException(rb.getString("orientation_required"));
    }
    try {
      return ImageOrientation.valueOf(x.toUpperCase());
    } catch (Exception e) {
      throw new ValidationException(rb.getString("orientation_invalid"));
    }
  }
}
