package com.acsredux.core.content.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TestAltString {

  @Test
  void testAltTextOfNull() {
    assertNull(AltText.of(null));
  }

  @Test
  void testExtensionTruncated() {
    assertEquals(new AltText("foo"), AltText.of(new FileName("foo.png")));
  }

  @Test
  void testNoExtension() {
    assertEquals(new AltText("foo"), AltText.of(new FileName("foo")));
  }
}
