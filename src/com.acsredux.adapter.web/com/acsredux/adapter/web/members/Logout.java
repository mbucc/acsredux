package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.auth.CookieAuthenticator.COOKIE_FMT;

import com.acsredux.adapter.web.common.FormData;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

class Logout {

  boolean isGetLogout(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/members/logout")
    );
  }

  void handleLogout(HttpExchange x1, FormData x2) {
    x1.getResponseHeaders().set("Location", "/");
    x1.getResponseHeaders().set("Set-cookie", String.format(COOKIE_FMT, "deleted", 0));
    try {
      x1.sendResponseHeaders(302, 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
