package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfNull;

public record SessionID(String val) {
  public SessionID(String val) {
    dieIfNull(val, "session ID");
    this.val = val;
  }
}
