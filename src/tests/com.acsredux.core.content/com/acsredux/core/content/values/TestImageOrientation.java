package com.acsredux.core.content.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

public class TestImageOrientation {

  ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");

  @Test
  void testSunnyParse() {
    assertEquals(
      ImageOrientation.PORTRAIT,
      ImageOrientation.of(ImageOrientation.PORTRAIT.name())
    );
    assertEquals(
      ImageOrientation.LANDSCAPE,
      ImageOrientation.of(ImageOrientation.LANDSCAPE.name())
    );
  }

  @Test
  void testNull() {
    var y = assertThrows(ValidationException.class, () -> ImageOrientation.of(null));
    assertEquals(rb.getString("orientation_required"), y.getMessage());
  }

  @Test
  void testInvalidString() {
    var y = assertThrows(ValidationException.class, () -> ImageOrientation.of("abc"));
    assertEquals(rb.getString("orientation_invalid"), y.getMessage());
  }
}
