package com.acsredux.adapter.web.common;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.adapter.web.MockHttpExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestFormData {

  FormData formData;

  @BeforeEach
  void setup() {
    this.formData = new FormData();
  }

  @Test
  void testOneValue() {
    // execute
    formData.add("test", "value");

    // verify
    assertEquals("value", formData.get("test"));
  }

  @Test
  void testNoValue() {
    // verify
    assertNull(formData.get("test"));
  }

  @Test
  void testTwoValues() {
    // execute
    formData.add("test", "a");
    formData.add("test", "b");

    // verify
    List<String> ys = formData.getAll("test");
    assertEquals(2, ys.size());
    assertEquals("a", ys.get(0));
    assertEquals("b", ys.get(1));
  }

  @Test
  void testOptionalField() {
    // setup
    var x = new MockHttpExchange(
      "/",
      "POST",
      "year=2022&name=&command=create_photo_diary".getBytes()
    );

    // execute
    FormData y = assertDoesNotThrow(() -> FormData.of(x));

    // verify
    assertEquals("2022", y.get("year"));
    assertEquals("create_photo_diary", y.get("command"));
    assertEquals("", y.get("name"));
  }

  @Test
  void testAsMap() {
    // setup
    formData.add("value", "1");
    formData.add("list", "a");
    formData.add("list", "b");

    // execute
    Map<String, Object> ys = formData.asMap();

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

  private enum PASSWORD_KEY {
    PWD1,
    PWD2,
    PASSWORD,
    PASSWD,
  }

  @ParameterizedTest
  @EnumSource
  void noPasswordsInToString(PASSWORD_KEY x) {
    // setup
    String key = x.name().toLowerCase();
    formData.add(key, "abc");
    formData.add("z", "def");

    // execute
    String y = formData.toString();

    // validate
    String exp = String.format("<FormData: %s=********, z=def>", key);
    assertEquals(exp, y);
  }

  @Test
  void testOfWorksWithEmptyString() {
    assertDoesNotThrow(() -> FormData.of(new MockHttpExchange("/")));
  }

  @Test
  void testThreeValues() {
    // setup
    var x = new MockHttpExchange(
      "/",
      "POST",
      "email=t%40t.com&pwd1=aBcd3fgh!&pwd2=aBcd3fgh!".getBytes()
    );

    // execute
    FormData y = FormData.of(x);

    // verify
    assertEquals("t@t.com", y.get("email"));
    assertEquals("aBcd3fgh!", y.get("pwd1"));
    assertEquals("aBcd3fgh!", y.get("pwd2"));
  }
}
