package com.acsredux.core.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.content.values.FileName;
import org.junit.jupiter.api.Test;

class TestFileName {

  @Test
  void testAddSuffix() {
    FileName x = new FileName("t.png");
    assertEquals(new FileName("t.orig.png"), x.insertSuffix("orig"));
  }

  @Test
  void testAddSuffixWithMultipleDots() {
    FileName x = new FileName("a.b.c.png");
    assertEquals(new FileName("a.b.c.orig.png"), x.insertSuffix("orig"));
  }

  @Test
  void testAddSuffixWithNoDots() {
    FileName x = new FileName("png");
    assertEquals(new FileName("orig.png"), x.insertSuffix("orig"));
  }

  @Test
  void testNullSuffix() {
    FileName x = new FileName("png");
    assertThrows(IllegalArgumentException.class, () -> x.insertSuffix(null));
  }
}
