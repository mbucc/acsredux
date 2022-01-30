package com.acsredux.adapter.web;

import org.junit.jupiter.api.Test;

class TestRootHandler {

  String expected(String url) {
    return "";
  }

  @Test
  void testSunnyPath() {
    // setup
    var handler = new RootHandler("./web/template");
    var mock = new MockHttpExchange("/");

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
