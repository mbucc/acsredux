package com.acsredux.core.content.values;

import com.acsredux.core.base.ValidationException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public record SectionIndex(int val) {
  public static final int MAX_SECTIONS = 200;

  public SectionIndex {
    if (val < 0) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(rb.getString("section_index_cannot_be_negative"));
    }
    if (val > MAX_SECTIONS) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(rb.getString("max_sections"), MAX_SECTIONS)
      );
    }
  }

  public static SectionIndex parse(String x) {
    if (x == null) {
      return null;
    }
    try {
      return new SectionIndex(Integer.parseInt(x));
    } catch (NumberFormatException e) {
      ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");
      throw new ValidationException(
        MessageFormat.format(rb.getString("invalid_integer"), x)
      );
    }
  }
}
