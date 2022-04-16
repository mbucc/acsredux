package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.Subject;
import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.ContentID;
import java.util.ResourceBundle;

public record AddNote(Subject subject, ContentID parentID, BlobBytes note)
  implements BaseContentCommand {
  public AddNote {
    var rb = ResourceBundle.getBundle("ContentErrorMessages");
    die(subject, "null subject");
    die(parentID, "null parent");
  }
}
