package com.acsredux.members.values;

import static com.acsredux.base.Util.dieIfNull;

public record MemberID(Long val) {
  public MemberID(Long val) {
    dieIfNull(val, "member ID");
    this.val = val;
  }
}
