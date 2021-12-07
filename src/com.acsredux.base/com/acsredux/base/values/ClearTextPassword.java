package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfBlank;

public record ClearTextPassword(String val) {
  public ClearTextPassword(String val) {
    dieIfBlank(val, "password");
    this.val = val;
  }
}
