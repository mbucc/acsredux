package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.TEST_DIARY_CONTENT_ID;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.NewContent;

public class MockContentWriter implements ContentWriter {

  @Override
  public ContentID save(NewContent x) {
    return TEST_DIARY_CONTENT_ID;
  }

  @Override
  public void update(Content y) {}

  @Override
  public void delete(ContentID x) {}
}
