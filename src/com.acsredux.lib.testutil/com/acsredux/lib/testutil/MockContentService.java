package com.acsredux.lib.testutil;

import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_DIARY;
import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_DIARY_CREATED;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public class MockContentService implements ContentService {

  @Override
  public Content getByID(ContentID x) {
    return TEST_PHOTO_DIARY;
  }

  @Override
  public List<Content> findByMemberID(MemberID x) {
    return List.of(TEST_PHOTO_DIARY);
  }

  @Override
  public List<Event> handle(BaseContentCommand x) {
    return List.of(TEST_PHOTO_DIARY_CREATED);
  }
}
