package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.Subject;
import com.acsredux.core.content.values.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record UploadPhoto(
  Subject subject,
  ContentID parent,
  FileName fileName,
  BlobBytes photoBytes,
  ImageOrientation orientation,
  ImageDate takenOn,
  ZoneId tz
)
  implements BaseContentCommand {
  public UploadPhoto {
    die(subject, "null Subject");
    die(parent, "null parent");
    die(fileName, "null fileName");
    die(photoBytes, "null photo bytes");
    die(orientation, "null orientation");
    die(takenOn, "null taken on");
    die(tz, "null time zone");
  }

  public Title title(ZoneId tz) {
    return new Title(
      takenOn().val().atZone(tz).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
    );
  }
}
