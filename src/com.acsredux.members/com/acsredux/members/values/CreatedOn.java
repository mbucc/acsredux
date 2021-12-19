package com.acsredux.members.values;

import static com.acsredux.base.Util.dieIfNull;

import java.time.Instant;

public record CreatedOn(Instant val) {
  public CreatedOn(Instant val) {
    dieIfNull(val, "created on");
    this.val = val;
  }
}
