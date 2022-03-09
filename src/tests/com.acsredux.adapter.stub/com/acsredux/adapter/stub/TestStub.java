package com.acsredux.adapter.stub;

import static com.acsredux.lib.testutil.TestData.TEST_CREATE_MEMBER_CMD;
import static com.acsredux.lib.testutil.TestData.TEST_CREATE_PHOTO_DIARY_COMMAND;
import static com.acsredux.lib.testutil.TestData.TEST_FILE_CONTENT;
import static com.acsredux.lib.testutil.TestData.TEST_FILE_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_STATUS;
import static com.acsredux.lib.testutil.TestData.TEST_SECTION_INDEX;
import static com.acsredux.lib.testutil.TestData.TEST_SUBJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TestStub {

  private CreatedOn now = new CreatedOn(Instant.now());

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
    assertEquals(2L, newMemberID.val());
    member = stub.findByID(newMemberID);
    assertTrue(member.isPresent());
  }

  @Test
  void testAddImage() {
    // setup
    var stub = new Stub();
    var now = new CreatedOn(Instant.now());
    stub.addMember(TEST_CREATE_MEMBER_CMD, TEST_MEMBER_STATUS, now);
    ContentID contentID = stub.createContent(TEST_CREATE_PHOTO_DIARY_COMMAND);
    var cmd = new UploadPhoto(
      TEST_SUBJECT,
      contentID,
      TEST_SECTION_INDEX,
      TEST_FILE_NAME,
      TEST_FILE_CONTENT
    );
    stub.addPhotoToDiary(cmd, TEST_FILE_NAME);

    // execute
    Content y = stub.getByID(contentID);

    // verify
    assertEquals(1, y.sections().get(TEST_SECTION_INDEX.val()).content().size());
  }
}
