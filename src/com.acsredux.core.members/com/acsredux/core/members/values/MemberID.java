package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.req;

public record MemberID(Long val) {
  public MemberID {
    req(val, "member ID");
  }
}
