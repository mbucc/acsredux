package com.acsredux.core.members.values;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.entities.Member;
import java.security.Principal;

public record MemberPrincipal(MemberID mid) implements Principal, ACSMemberPrincipal {
  @Override
  public String getName() {
    return String.valueOf(mid().val());
  }

  public static MemberPrincipal of(Member x) {
    return new MemberPrincipal(x.id());
  }
}
