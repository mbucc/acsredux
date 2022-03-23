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
import org.junit.jupiter.api.Disabled;
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
    assertEquals(2L, newMemberID.val());
    member = stub.findByID(newMemberID);
    assertTrue(member.isPresent());
  }

  @Disabled("FIX ME")
  @Test
  void testAddImage() {
    // setup
    var stub = new Stub();
    var now = new CreatedOn(Instant.now());
    stub.addMember(TEST_CREATE_MEMBER_CMD, TEST_MEMBER_STATUS, now);
    ContentID contentID = stub.save(TEST_NEW_DIARY_CONTENT);
    //stub.addPhotoToDiary(now, contentID, TEST_SECTION_INDEX, TEST_IMAGE);

    // execute
    Content y = stub.getByID(contentID);
    // verify
    //assertEquals(2, ys.size());
  }
}
