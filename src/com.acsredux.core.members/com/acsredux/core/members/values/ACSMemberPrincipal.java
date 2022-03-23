package com.acsredux.core.members.values;

import com.acsredux.core.base.MemberID;

public sealed interface ACSMemberPrincipal permits MemberPrincipal, AnonymousPrincipal {
  default MemberID mid() {
    if (this instanceof MemberPrincipal o) {
      return o.mid();
    } else {
      return null;
    }
  }
}
