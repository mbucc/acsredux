package com.acsredux.adapter.web;

import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.adapter.web.auth.AnonymousPrincipal;
import com.github.difflib.text.DiffRowGenerator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class MockHttpExchange extends HttpExchange {

  public static final String DEFAULT_REQUEST_BODY = "Hello world!";
  public static final String DEFAULT_REQUEST_METHOD = "GET";

  final String url;
  final String requestMethod;
  final String requestBody;
  HttpPrincipal principal = new AnonymousPrincipal("Test User");
  String goldenSuffix = "";
  final String testDir = projectRoot() + "/test/web/gold";
  String webDir = projectRoot() + "/web/template";

  public static String projectRoot() {
    String cwd = System.getProperty("user.dir");
    return cwd.contains("acsredux/src/") ? "../../" : ".";
  }

  final Headers responseHeaders = new Headers();
  final Headers requestHeaders = new Headers();
  final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
  int responseCode;
  long responseLength;

  public MockHttpExchange(String url) {
    this(url, DEFAULT_REQUEST_METHOD, DEFAULT_REQUEST_BODY);
  }

  public MockHttpExchange(String url, String requestMethod) {
    this(url, requestMethod, DEFAULT_REQUEST_BODY);
  }

  public MockHttpExchange(String url, String requestMethod, String body) {
    this.url = url;
    this.requestMethod = requestMethod;
    this.requestBody = body;
  }

  public void close() {}

  public Object getAttribute(String name) {
    return null;
  }

  public HttpContext getHttpContext() {
    return null;
  }

  public InetSocketAddress getLocalAddress() {
    return null;
  }

  public HttpPrincipal getPrincipal() {
    return this.principal;
  }

  public String getProtocol() {
    return null;
  }

  public InetSocketAddress getRemoteAddress() {
    return null;
  }

  public InputStream getRequestBody() {
    return new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
  }

  public Headers getRequestHeaders() {
    return this.requestHeaders;
  }

  public String getRequestMethod() {
    return this.requestMethod;
  }

  public URI getRequestURI() {
    try {
      return new URI(this.url);
    } catch (Exception e) {
      throw new IllegalStateException("can't create URI from url: " + this.url);
    }
  }

  public OutputStream getResponseBody() {
    return this.responseBody;
  }

  public int getResponseCode() {
    return 200;
  }

  public Headers getResponseHeaders() {
    return this.responseHeaders;
  }

  public void sendResponseHeaders(int rCode, long responseLength) {
    this.responseCode = rCode;
    this.responseLength = responseLength;
  }

  public void setAttribute(String name, Object value) {}

  public void setStreams(InputStream i, OutputStream o) {}

  String expected() {
    try {
      return Files.readString(expectedPath());
    } catch (IOException e) {
      throw new IllegalStateException("can't create diff", e);
    }
  }

  public String actual() {
    var buf = new StringBuilder();
    buf.append(this.responseCode);
    buf.append("\n");
    buf.append("Content-Length: ");
    buf.append(this.responseLength);
    buf.append("\n");
    this.responseHeaders.forEach((k, v) ->
        v.forEach(v1 -> {
          buf.append(k);
          buf.append(": ");
          buf.append(v1);
          buf.append("\n");
        })
      );
    buf.append("\n");
    buf.append(this.responseBody.toString(StandardCharsets.UTF_8));
    return buf.toString();
  }

  void writeActual() {
    try {
      Files.writeString(actualPath(), actual());
    } catch (IOException e) {
      String fmt = "can't write actual file %s with cwd = %s";
      throw new IllegalStateException(
        String.format(fmt, actualPath(), System.getProperty("user.dir")),
        e
      );
    }
  }

  String diff() {
    try {
      List<String> xs = Files.readAllLines(expectedPath());
      List<String> ys = Files.readAllLines(actualPath());
      String addPrefix = " + ";
      String delPrefix = " - ";
      var buf = new StringBuilder();
      buf.append("\n");
      buf.append(delPrefix);
      buf.append(expectedPath().toString());
      buf.append("\n");
      buf.append(addPrefix);
      buf.append(actualPath().toString());
      buf.append("\n");
      buf.append("----------------------------------------------------\n");
      buf.append("\n");
      DiffRowGenerator generator = DiffRowGenerator
        .create()
        .reportLinesUnchanged(true)
        .build();
      generator
        .generateDiffRows(xs, ys)
        .forEach(z -> {
          switch (z.getTag()) {
            case INSERT -> {
              buf.append(addPrefix);
              buf.append(z.getNewLine());
              buf.append("\n");
            }
            case DELETE -> {
              buf.append(delPrefix);
              buf.append(z.getOldLine());
              buf.append("\n");
            }
            case CHANGE -> {
              buf.append(delPrefix);
              buf.append(z.getOldLine());
              buf.append("\n");
              buf.append(addPrefix);
              buf.append(z.getNewLine());
              buf.append("\n");
            }
            default -> {}
            /* EMPTY */
          }
        });
      return buf.toString();
    } catch (IOException e) {
      throw new IllegalStateException("can't generate diff", e);
    }
  }

  String html() {
    String y = this.url;
    if (this.url.endsWith("/")) {
      y += "index.html";
    }
    if (this.requestMethod.equals("POST")) {
      y += ".post";
    }
    if (!this.goldenSuffix.isBlank()) {
      y += "-" + this.goldenSuffix;
    }
    return y;
  }

  Path actualPath() {
    return FileSystems.getDefault().getPath(testDir, html() + ".actual");
  }

  Path expectedPath() {
    return FileSystems.getDefault().getPath(testDir, html() + ".golden");
  }

  public void goldTest() {
    writeActual();
    if (!expected().equals(actual())) {
      System.out.println(diff());
      fail("actual output did not match expected output");
    }
  }

  public void setPrincipal(HttpPrincipal x) {
    this.principal = x;
  }

  public void setGoldenSuffix(String x) {
    this.goldenSuffix = Objects.requireNonNullElse(x, "");
  }

  @Override
  public String toString() {
    return "MockHttpExchange: " + this.requestMethod + " " + this.url;
  }
}