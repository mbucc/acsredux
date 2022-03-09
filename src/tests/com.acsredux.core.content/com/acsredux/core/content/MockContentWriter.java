package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.TEST_CONTENT_ID;

import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.Image;
import com.acsredux.core.content.values.SectionIndex;

public class MockContentWriter implements ContentWriter {

  @Override
  public ContentID createContent(CreatePhotoDiary x) {
    return TEST_CONTENT_ID;
  }

  @Override
  public void addPhotoToDiary(ContentID x1, SectionIndex x2, Image x3) {}
}
