package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.Subject;
import com.acsredux.core.content.values.ContentID;

public record DeleteContent(Subject subject, ContentID contentID)
  implements BaseContentCommand {
  public DeleteContent {
    die(subject, "null Subject");
    die(contentID, "null contentID");
  }
}
