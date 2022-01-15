package com.acsredux.core.members.values;

import static com.acsredux.core.base.Util.dieIfBlank;

import java.util.ResourceBundle;

public record LastName(String val) {
  public LastName(String val) {
    var errors = ResourceBundle.getBundle("MemberErrorMessages");
    dieIfBlank(val, errors.getString("lastname_missing"));
    this.val = val;
  }
}
