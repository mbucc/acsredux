package com.acsredux.adapter.stub;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.MemberStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TestStub {

  private final CreatedOn now = new CreatedOn(Instant.now());

  @Test
  void testAddMember() {
    // setup
    var stub = new Stub();

    var member = stub.findByID(TEST_MEMBER_ID);
    assertFalse(member.isPresent());

    // execute
    var newMemberID = stub.addMember(
      TEST_CREATE_MEMBER_CMD,
      MemberStatus.NEEDS_EMAIL_VERIFICATION,
      now
    );

    // verify
    assertEquals(1L, newMemberID.val());
    member = stub.findByID(newMemberID);
    assertTrue(member.isPresent());
  }

  @Test
  void testAddContent() {
    // setup
    var stub = new Stub();
    var now = new CreatedOn(Instant.now());
    stub.addMember(TEST_CREATE_MEMBER_CMD, TEST_MEMBER_STATUS, now);
    ContentID diaryID = stub.save(TEST_NEW_CONTENT_DIARY);

    // execute
    Content y = stub.getByID(diaryID);

    // verify
    Content exp = new Content(
      new ContentID(1L),
      TEST_PHOTO_DIARY_CONTENT.refersTo(),
      TEST_PHOTO_DIARY_CONTENT.createdBy(),
      TEST_PHOTO_DIARY_CONTENT.title(),
      TEST_PHOTO_DIARY_CONTENT.createdOn(),
      TEST_PHOTO_DIARY_CONTENT.from(),
      TEST_PHOTO_DIARY_CONTENT.upto(),
      TEST_PHOTO_DIARY_CONTENT.contentType(),
      TEST_PHOTO_DIARY_CONTENT.blobType(),
      TEST_PHOTO_DIARY_CONTENT.content()
    );
    assertEquals(exp, y);
  }
}
