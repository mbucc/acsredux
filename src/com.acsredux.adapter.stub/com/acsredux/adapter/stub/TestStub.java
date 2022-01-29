package com.acsredux.adapter.stub;

import static com.acsredux.lib.testutil.TestData.TEST_ADD_MEMBER_CMD;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.core.members.values.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TestStub {

  @Test
  void testAddMember() {
    // setup
    var stub = Stub.provider();
    var now = new CreatedOn(Instant.now());
    var dash = stub.findMemberDashboard(TEST_MEMBER_ID);
    assertFalse(dash.isPresent());

    // execute
    var newMemberID = stub.addMember(
      TEST_ADD_MEMBER_CMD,
      MemberStatus.NEEDS_EMAIL_VERIFICATION,
      now
    );

    // verify
    assertEquals(2L, newMemberID.val());
    dash = stub.findMemberDashboard(newMemberID);
    assertTrue(dash.isPresent());
  }
}
