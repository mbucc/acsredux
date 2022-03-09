package com.acsredux.core.content.ports;

import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.FileName;

public interface ContentWriter {
  ContentID createContent(CreatePhotoDiary x);
  void addPhotoToDiary(UploadPhoto x1, FileName x3);
}
