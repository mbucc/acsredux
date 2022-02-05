package com.acsredux.adapter.web.auth;

import com.sun.net.httpserver.HttpPrincipal;

public class AnonymousPrincipal extends HttpPrincipal {

  public AnonymousPrincipal(String username) {
    super(username, "/");
  }
}
