package com.acsredux.adapter.web.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.Headers;
import java.net.HttpCookie;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestFindCookie {

  private Headers headers;

  @BeforeEach
  void setup() {
    this.headers = new Headers();
  }

  @Test
  void testNoCookie() {
    // execute
    Optional<HttpCookie> y = CookieAuthenticator.findAuthCookie(headers);

    // verify
    assertEquals(Optional.empty(), y);
  }

  @Test
  void testOneSessionCookie() {
    // setup
    headers.add("Cookie", "session_id=abc123");

    // execute
    Optional<HttpCookie> y = CookieAuthenticator.findAuthCookie(headers);

    // verify
    assertTrue(y.isPresent());
    assertEquals("abc123", y.get().getValue());
  }

  @Test
  void testTwoCookiesOneTheSameLine() {
    // setup
    headers.add("Cookie", "lang=en_US; session_id=abc123");

    // execute
    Optional<HttpCookie> y = CookieAuthenticator.findAuthCookie(headers);

    // verify
    assertTrue(y.isPresent());
    assertEquals("abc123", y.get().getValue());
  }

  @Test
  void testTwoCookies() {
    // setup
    headers.add("Cookie", "session_id=abc123");
    headers.add("Cookie", "lang=en_US");

    // execute
    Optional<HttpCookie> y = CookieAuthenticator.findAuthCookie(headers);

    // verify
    assertTrue(y.isPresent());
    assertEquals("abc123", y.get().getValue());
  }
}
