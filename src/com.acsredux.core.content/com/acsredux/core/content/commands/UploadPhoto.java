package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.content.values.*;
import javax.security.auth.Subject;

public record UploadPhoto(
  Subject subject,
  ContentID contentID,
  SectionIndex sectionIndex,
  FileName fileName,
  FileContent content,
  ImageOrientation orientation,
  ImageDate date
)
  implements BaseContentCommand {
  public UploadPhoto {
    die(subject, "null Subject");
    die(contentID, "null contentID");
    die(sectionIndex, "null sectionIndex");
    die(fileName, "null fileName");
    die(content, "null content");
  }
}
