package com.acsredux.members.values;

import static com.acsredux.base.Util.dieIfBlank;

public record FirstName(String val) {
  public FirstName(String val) {
    dieIfBlank(val, "first name");
    this.val = val;
  }
}
