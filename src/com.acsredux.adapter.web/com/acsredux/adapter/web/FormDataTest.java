package com.acsredux.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormDataTest {

  FormData x;

  @BeforeEach
  void setup() {
    this.x = new FormData();
  }

  @Test
  void testOneValue() {
    // execute
    x.add("test", "value");

    // verify
    assertEquals("value", x.get("test"));
  }

  @Test
  void testNoValue() {
    // verify
    assertNull(x.get("test"));
  }

  @Test
  void testTwoValues() {
    // execute
    x.add("test", "a");
    x.add("test", "b");

    // verify
    List<String> ys = x.getAll("test");
    assertEquals(2, ys.size());
    assertEquals("a", ys.get(0));
    assertEquals("b", ys.get(1));
  }

  @Test
  void testKeysAreCaseInsensitive() {
    // setup
    x.add("AbC", "dEf");

    // execute
    String y = x.get("abc");

    // validate
    assertNotNull(y);
    assertEquals("dEf", y);
  }

  @Test
  void testAsMap() {
    // setup
    x.add("value", "1");
    x.add("list", "a");
    x.add("list", "b");

    // execute
    Map<String, Object> ys = x.asMap();

    // verify
    assertNotNull(ys);
    assertEquals(2, ys.size());
    assertNotNull(ys.get("value"));
    assertTrue(ys.get("value") instanceof String);
    assertTrue(ys.get("list") instanceof List);
    @SuppressWarnings("unchecked")
    List<String> ys1 = (ArrayList<String>) ys.get("list");
    assertEquals("a", ys1.get(0));
    assertEquals("b", ys1.get(1));
  }
}
