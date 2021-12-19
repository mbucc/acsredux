package com.acsredux.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ParseDataTest {

  @Test
  void testOneValue() {
    // setup
    var formdata = "email=t%40t.com&pwd1=aBcd3fgh!&pwd2=aBcd3fgh!";

    // execute
    FormData y = Util.parseFormData(formdata);

    // verify
    assertEquals("t@t.com", y.get("email"));
    assertEquals("aBcd3fgh!", y.get("pwd1"));
    assertEquals("aBcd3fgh!", y.get("pwd2"));
  }
}
