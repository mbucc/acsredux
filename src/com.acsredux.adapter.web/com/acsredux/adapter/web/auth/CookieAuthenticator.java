package com.acsredux.adapter.web.auth;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class CookieAuthenticator extends Authenticator {

  public Authenticator.Result authenticate(HttpExchange exch) {
    return new Authenticator.Success(new HttpPrincipal("Anonymous", "/"));
  }
}
