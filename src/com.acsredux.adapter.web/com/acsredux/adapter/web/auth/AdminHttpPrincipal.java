package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.entities.Member;
import com.sun.net.httpserver.HttpPrincipal;

public final class AdminHttpPrincipal extends HttpPrincipal implements ACSHttpPrincipal {

  private final Member member;

  public AdminHttpPrincipal(Member x) {
    super(x.fullName(), "/");
    this.member = x;
  }

  public Member getMember() {
    return this.member;
  }
}
