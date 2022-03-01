package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.req;

import java.time.Instant;

public record LoginTime(Instant val) {
  public LoginTime {
    req(val, "LoginTime");
  }

  public static LoginTime of(Instant x) {
    return x == null ? null : new LoginTime(x);
  }
}
