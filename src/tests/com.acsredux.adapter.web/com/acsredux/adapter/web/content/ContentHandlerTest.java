package com.acsredux.adapter.web.content;

import static com.acsredux.adapter.web.MockHttpExchange.TEST_HTTP_PRINCIPAL;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.lib.testutil.MockContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentHandlerTest {

  ContentHandler handler;
  MockContentService mockContentService;

  @BeforeEach
  void setup() {
    this.mockContentService = new MockContentService();
    this.handler = new ContentHandler(this.mockContentService);
  }

  @Test
  void testPutBody() {
    // setup
    var mock = new MockHttpExchange("/content/4321/body", "PUT", "abc123def");
    mock.setPrincipal(TEST_HTTP_PRINCIPAL);

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
