package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

public record Email(String val) {
  public Email(String val) {
    dieIfBlank(val, "email");
    this.val = val;
  }
}
