package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.req;

import java.time.Instant;

public record UpdatedOn(Instant val) {
  public UpdatedOn {
    req(val, "created on");
  }
}
