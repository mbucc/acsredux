package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

public record FirstName(String val) {
  public FirstName(String val) {
    dieIfBlank(val, "first name");
    this.val = val;
  }
}
