package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

public record LastName(String val) {
  public LastName(String val) {
    dieIfBlank(val, "last name");
    this.val = val;
  }
}
