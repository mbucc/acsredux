package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfNull;

public record UserID(Long val) {
  public UserID(Long val) {
    dieIfNull(val, "user ID");
    this.val = val;
  }
}
