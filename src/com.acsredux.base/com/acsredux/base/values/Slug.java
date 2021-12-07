package com.acsredux.base.values;

import static com.acsredux.base.Util.dieIfBlank;

public record Slug(String val) {
  public Slug(String val) {
    dieIfBlank(val, "slug");
    this.val = val;
  }
}
