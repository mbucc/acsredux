package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

public record GrowingZone(String val) {
  public GrowingZone(String val) {
    dieIfBlank(val, "growing zone");
    this.val = val;
  }
}
