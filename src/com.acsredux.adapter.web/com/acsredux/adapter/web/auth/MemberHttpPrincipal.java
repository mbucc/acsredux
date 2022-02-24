package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.entities.Member;
import com.sun.net.httpserver.HttpPrincipal;

public class MemberHttpPrincipal extends HttpPrincipal {

  private final Member member;

  public MemberHttpPrincipal(Member x) {
    super(x.fullName(), "/");
    this.member = x;
  }

  public Member getMember() {
    return this.member;
  }
}
