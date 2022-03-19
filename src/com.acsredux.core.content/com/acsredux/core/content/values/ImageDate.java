package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public record ImageDate(LocalDate date, LocalTime time) {
  public ImageDate {
    die(date, "date is required");
  }

  public static ImageDate of(String x) {
    if (x == null) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("invalid_image_date"));
    }
    try {
      LocalDateTime y1 = LocalDateTime.parse(x);
      return new ImageDate(y1.toLocalDate(), y1.toLocalTime());
    } catch (DateTimeParseException e) {
      /* FALL THROUGH */
    }

    try {
      LocalDate y1 = LocalDate.parse(x);
      return new ImageDate(y1, null);
    } catch (DateTimeParseException e) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("invalid_image_date"));
    }
  }
}
