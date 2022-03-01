package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.req;

import java.time.Instant;

public record CreatedOn(Instant val) {
  public CreatedOn {
    req(val, "created on");
  }
}
