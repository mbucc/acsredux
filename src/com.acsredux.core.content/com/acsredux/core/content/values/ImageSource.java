package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

// Use String not URL type because "/static/img/t.png" is not a valid URL.
public record ImageSource(String val) {
  public ImageSource {
    die(val, "ImageSource value cannot be empty.");
  }
}
