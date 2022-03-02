package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_DIARY;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public class MockContentReader implements ContentReader {

  @Override
  public Content getContent(ContentID x) {
    return TEST_PHOTO_DIARY;
  }

  @Override
  public List<Content> findContentByMemberID(MemberID x) {
    return List.of(TEST_PHOTO_DIARY);
  }
}
