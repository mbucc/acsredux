package com.acsredux.adapter.stub;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.members.commands.AddMember;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class StubTest {

  @Test
  void testFindDashboard() {
    // setup
    var stub = Stub.provider();
    var cmd = new AddMember(
      null,
      null,
      new Email("t@t.com"),
      new ClearTextPassword("aBcd3fg!"),
      new ClearTextPassword("aBcd3fg!"),
      new GrowingZone("5A")
    );
    var now = new CreatedOn(Instant.now());
    var newMemberID = stub.addMember(cmd, now);
    MemberID x = new MemberID(newMemberID.val());
    var query = new FindMemberDashboard(x);

    // execute
    var dash = stub.findMemberDashboard(query);

    // verify
    assertTrue(dash.isPresent());
  }
}
