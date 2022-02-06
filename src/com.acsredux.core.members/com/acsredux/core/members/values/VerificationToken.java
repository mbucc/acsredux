package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

public record VerificationToken(String val) {
  public VerificationToken {
    dieIfBlank(val, "verification token");
  }
}
