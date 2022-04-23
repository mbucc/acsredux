package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

public record NewContentText(ContentID id, BlobBytes content) {
  public NewContentText {
    die(id, "null contentID");
  }
}
