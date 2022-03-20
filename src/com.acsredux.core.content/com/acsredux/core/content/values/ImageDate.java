package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.ValidationException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public record ImageDate(Instant val) {
  public ImageDate {
    die(val, "ImageDate cannot be null");
  }

  public static ImageDate of(String x, ZoneId tz) {
    if (x == null) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("invalid_image_date"));
    }
    try {
      LocalDateTime y1 = LocalDateTime.parse(x);
      return new ImageDate(y1.atZone(tz).toInstant());
    } catch (DateTimeParseException e) {
      /* FALL THROUGH */
    }

    try {
      LocalDate y1 = LocalDate.parse(x);
      return new ImageDate(y1.atStartOfDay(tz).toInstant());
    } catch (DateTimeParseException e) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("invalid_image_date"));
    }
  }
}
