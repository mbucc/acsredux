package com.acsredux.adapter.web.auth;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.entities.Member;
import com.sun.net.httpserver.HttpPrincipal;

public final class MemberHttpPrincipal extends HttpPrincipal implements ACSHttpPrincipal {

  private final Member member;

  public MemberHttpPrincipal(Member x) {
    super(x.fullName(), "/");
    this.member = x;
  }

  public Member getMember() {
    return this.member;
  }

  public MemberID getID() {
    return this.member.id();
  }
}
