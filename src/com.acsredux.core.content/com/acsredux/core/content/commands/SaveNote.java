package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.Subject;
import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.FromDateTime;
import java.util.ResourceBundle;

public record SaveNote(
  Subject subject,
  ContentID parentID,
  FromDateTime from,
  BlobBytes body
)
  implements BaseContentCommand {
  public SaveNote {
    var rb = ResourceBundle.getBundle("ContentErrorMessages");
    die(subject, "null subject");
    die(from, "null from date/time");
    die(parentID, "null parent");
  }
}
