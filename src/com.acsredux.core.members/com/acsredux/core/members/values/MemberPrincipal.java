package com.acsredux.core.members.values;

import java.security.Principal;

public record MemberPrincipal(MemberID mid) implements Principal {
  @Override
  public String getName() {
    return String.valueOf(mid());
  }
}
