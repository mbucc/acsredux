package com.acsredux.core.members.values;

public sealed interface ACSMemberPrincipal permits MemberPrincipal, AnonymousPrincipal {
  default MemberID mid() {
    if (this instanceof MemberPrincipal o) {
      return o.mid();
    }
    throw new IllegalStateException("An anonymous principal has no ID.");
  }
}
