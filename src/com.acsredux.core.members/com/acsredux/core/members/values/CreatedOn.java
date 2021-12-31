package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfNull;

import java.time.Instant;

public record CreatedOn(Instant val) {
  public CreatedOn(Instant val) {
    dieIfNull(val, "created on");
    this.val = val;
  }
}
