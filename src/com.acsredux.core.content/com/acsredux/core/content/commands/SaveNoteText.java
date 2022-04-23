package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.Subject;
import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.ContentID;
import java.util.ResourceBundle;

public record SaveNoteText(Subject subject, ContentID id, BlobBytes text)
  implements BaseContentCommand {
  public SaveNoteText {
    var rb = ResourceBundle.getBundle("ContentErrorMessages");
    die(subject, "null subject");
    die(id, "null ID");
  }
}
