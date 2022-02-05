package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.entities.Member;
import com.sun.net.httpserver.HttpPrincipal;

public class MemberPrincipal extends HttpPrincipal {

  private final Member member;

  public MemberPrincipal(Member x) {
    super(x.fullname(), "/");
    this.member = x;
  }

  public Member getMember() {
    return this.member;
  }
}
