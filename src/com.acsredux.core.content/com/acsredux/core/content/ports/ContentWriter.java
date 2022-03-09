package com.acsredux.core.content.ports;

import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.Image;
import com.acsredux.core.content.values.SectionIndex;

public interface ContentWriter {
  ContentID createContent(CreatePhotoDiary x);
  void addPhotoToDiary(ContentID x1, SectionIndex x2, Image x3);
}
