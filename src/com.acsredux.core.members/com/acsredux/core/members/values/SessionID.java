package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.req;

public record SessionID(String val) {
  public SessionID {
    req(val, "session ID");
  }

  public static SessionID of(String x) {
    return x == null ? null : new SessionID(x);
  }
}
