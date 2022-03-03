package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.req;

public record MemberID(Long val) {
  public MemberID {
    req(val, "member ID");
  }

  public static MemberID parse(String x) {
    try {
      return new MemberID(Long.parseLong(x));
    } catch (Exception e) {
      throw new IllegalStateException("invalid member id '" + x + "'");
    }
  }
}
