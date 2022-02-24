package com.acsredux.core.members.values;

import static com.acsredux.core.members.MemberService.ANONYMOUS_USERNAME;

import java.security.Principal;

public record AnonymousPrincipal() implements Principal {
  @Override
  public String getName() {
    return ANONYMOUS_USERNAME;
  }
}
