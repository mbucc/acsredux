package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.entities.Member;

public class AdminHttpPrincipal extends MemberHttpPrincipal {

  public AdminHttpPrincipal(Member x) {
    super(x);
  }
}
