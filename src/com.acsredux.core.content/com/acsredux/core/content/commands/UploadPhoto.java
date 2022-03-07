package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.FileContent;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.content.values.SectionIndex;
import java.util.ResourceBundle;
import javax.security.auth.Subject;

public record UploadPhoto(
  Subject subject,
  ContentID contentID,
  SectionIndex sectionIndex,
  FileName fileName,
  FileContent content
)
  implements BaseContentCommand {
  public UploadPhoto {
    var rb = ResourceBundle.getBundle("ContentErrorMessages");
    die(subject, "null Subject");
    die(contentID, "null contentID");
    die(sectionIndex, "null sectionIndex");
    die(fileName, "null fileName");
    die(content, "null content");
  }
}
