package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

import java.util.ResourceBundle;

public record FirstName(String val) {
  public FirstName(String val) {
    var errors = ResourceBundle.getBundle("MemberErrorMessages");
    dieIfBlank(val, errors.getString("firstname_missing"));
    this.val = val.trim();
  }
}
