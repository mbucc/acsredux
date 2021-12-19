package com.acsredux.members.values;

import static com.acsredux.base.Util.dieIfBlank;

public record GrowingZone(String val) {
  public GrowingZone(String val) {
    dieIfBlank(val, "growing zone");
    this.val = val;
  }
}
