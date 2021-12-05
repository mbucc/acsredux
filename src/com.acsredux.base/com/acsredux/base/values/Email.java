package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfBlank;

public record Email(String val) {
  public Email(String val) {
    dieIfBlank(val, "email");
    this.val = val;
  }
}
