package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.TEST_CONTENT_ID;

import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.values.ContentID;

public class MockContentWriter implements ContentWriter {

  @Override
  public ContentID createContent(CreatePhotoDiary x) {
    return TEST_CONTENT_ID;
  }
}
