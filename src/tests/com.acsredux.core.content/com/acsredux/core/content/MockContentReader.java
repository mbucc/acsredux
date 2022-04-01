package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.TEST_COMMENT;
import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_CONTENT;
import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_CONTENT_ID;
import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_DIARY_CONTENT;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.values.ContentID;
import java.util.List;

public class MockContentReader implements ContentReader {

  @Override
  public Content getByID(ContentID x) {
    if (x.equals(TEST_PHOTO_CONTENT_ID)) {
      return TEST_PHOTO_CONTENT;
    } else {
      return TEST_PHOTO_DIARY_CONTENT;
    }
  }

  @Override
  public List<Content> findByMemberID(MemberID x) {
    return List.of(TEST_PHOTO_DIARY_CONTENT);
  }

  @Override
  public List<Content> findChildrenOfID(ContentID x) {
    return List.of(TEST_COMMENT);
  }
}
