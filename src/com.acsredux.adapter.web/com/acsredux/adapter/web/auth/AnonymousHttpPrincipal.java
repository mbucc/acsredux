package com.acsredux.adapter.web.auth;

import com.sun.net.httpserver.HttpPrincipal;

public class AnonymousHttpPrincipal extends HttpPrincipal {

  public AnonymousHttpPrincipal(String username) {
    super(username, "/");
  }
}
