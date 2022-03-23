package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

import java.time.Instant;

public record UptoDateTime(Instant val) {
  public UptoDateTime {
    die(val, "null val");
  }
}
