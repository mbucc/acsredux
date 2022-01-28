package com.acsredux.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.NotFoundException;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestBaseHandler {

  static class TestHandler extends BaseHandler {

    List<Route> routes = new ArrayList<>();

    List<BaseHandler.Route> getRoutes() {
      return routes;
    }
  }

  private TestHandler handler;

  @BeforeEach
  void setup() {
    this.handler = new TestHandler();
  }

  @Test
  void testPageNotFound() {
    // setup
    var mock = new MockHttpExchange("/");

    // execute and verify
    NotFoundException y = assertThrows(
      NotFoundException.class,
      () -> handler.handle(mock)
    );
  }

  private void addRoute(BiConsumer<HttpExchange, FormData> x) {
    this.handler.routes.add(new BaseHandler.Route(this::alwaysTrue, x));
  }

  private void setResponse(HttpExchange x1, byte[] body) {
    x1.getResponseHeaders().set("Content-type", "text/html; charset= UTF-8");
    try {
      x1.sendResponseHeaders(200, body.length);
      x1.getResponseBody().write(body);
    } catch (IOException e) {
      throw new IllegalStateException("error sending response", e);
    }
  }

  private boolean alwaysTrue(HttpExchange x) {
    return true;
  }

  private void helloWorld(HttpExchange x1, FormData x2) {
    setResponse(x1, "Hello World".getBytes());
  }

  private void formPost(HttpExchange x1, FormData x2) {
    StringBuilder buf = new StringBuilder();
    buf.append("Form values posted:\n");
    buf.append(x2.toString());
    setResponse(x1, buf.toString().getBytes());
  }

  private void throws404(HttpExchange x1, FormData x2) {
    throw new NotFoundException("foobar not found");
  }

  private void throws500(HttpExchange x1, FormData x2) {
    throw new IllegalStateException("boom!");
  }

  @Test
  void testHelloWorld() {
    // setup
    addRoute(this::helloWorld);
    var mock = new MockHttpExchange("/");

    // execute and verify
    handler.handle(mock);

    // verify
    String expected =
      """
      200
      Content-Length: 11
      Content-type: text/html; charset= UTF-8
      
      Hello World""";
    assertEquals(expected, mock.actual());
  }

  @Test
  void testFormPost() {
    // setup
    addRoute(this::formPost);
    var mock = new MockHttpExchange("/", "POST", "firstname=foo");

    // execute
    handler.handle(mock);

    // verify
    String expected =
      """
      200
      Content-Length: 45
      Content-type: text/html; charset= UTF-8
      
      Form values posted:
      <FormData: firstname=foo>""";
    String actual = mock.actual();
    assertEquals(expected, mock.actual());
  }

  @Test
  void test404() {
    // setup
    addRoute(this::throws404);
    var mock = new MockHttpExchange("/");
    this.handler.setErr(new PrintStream(OutputStream.nullOutputStream()));

    // execute
    handler.handle(mock);

    // verify
    String expected =
      """
      404
      Content-Length: 10
      Content-type: text/html; charset= UTF-8
      
      Not found.""";
    assertEquals(expected, mock.actual());
  }

  @Test
  void test500() {
    // setup
    addRoute(this::throws500);
    var mock = new MockHttpExchange("/");
    this.handler.setErr(new PrintStream(OutputStream.nullOutputStream()));

    // execute
    handler.handle(mock);

    // verify
    String expected =
      """
      500
      Content-Length: 15
      Content-type: text/html; charset= UTF-8
      
      Internal error.""";
    assertEquals(expected, mock.actual());
  }
}
