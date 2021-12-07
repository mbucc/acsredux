package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfBlank;

public record LastName(String val) {
  public LastName(String val) {
    dieIfBlank(val, "last name");
    this.val = val;
  }
}
