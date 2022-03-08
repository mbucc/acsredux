package com.acsredux.core.content.ports;

import com.acsredux.core.content.events.ImageSavedEvent;
import com.acsredux.core.content.values.FileContent;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.members.values.MemberID;
import java.time.Instant;

public interface ImageWriter {
  ImageSavedEvent save(Instant instant, MemberID x1, FileContent x2, FileName x3);

  FileContent resize(FileContent x1, int pixelHeight);
}
