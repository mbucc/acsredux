package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfNull;

public record MemberID(Long val) {
  public MemberID {
    dieIfNull(val, "member ID");
  }
}
