package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.MemberService;
import com.sun.net.httpserver.HttpPrincipal;

public final class AnonymousHttpPrincipal
  extends HttpPrincipal
  implements ACSHttpPrincipal {

  public AnonymousHttpPrincipal() {
    super(MemberService.ANONYMOUS_USERNAME, "/");
  }
}
