package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.MemberService;
import com.sun.net.httpserver.HttpPrincipal;

public class AnonymousHttpPrincipal extends HttpPrincipal {

  public AnonymousHttpPrincipal() {
    super(MemberService.ANONYMOUS_USERNAME, "/");
  }
}
