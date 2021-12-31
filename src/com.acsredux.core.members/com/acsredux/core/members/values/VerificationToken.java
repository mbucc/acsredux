package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

public record VerificationToken(String val) {
  public VerificationToken(String val) {
    dieIfBlank(val, "verification token");
    this.val = val;
  }
}
