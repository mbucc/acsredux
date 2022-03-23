package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

import java.time.Instant;

public record FromDateTime(Instant val) {
  public FromDateTime {
    die(val, "null val");
  }
}
