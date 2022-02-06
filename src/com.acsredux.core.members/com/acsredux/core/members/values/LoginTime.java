package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfNull;

import java.time.Instant;

public record LoginTime(Instant val) {
  public LoginTime {
    dieIfNull(val, "LoginTime");
  }

  public static LoginTime of(Instant x) {
    return x == null ? null : new LoginTime(x);
  }
}
