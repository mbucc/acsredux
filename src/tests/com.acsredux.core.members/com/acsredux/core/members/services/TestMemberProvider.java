package com.acsredux.core.members.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.lib.testutil.MockProxy;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberProvider {

  private MemberService service;
  private MemberReader reader;

  @BeforeEach
  void setup() {
    reader = (MemberReader) MockProxy.of(new MockMemberReader());
    MemberWriter writer = (MemberWriter) MockProxy.of(new MockMemberWriter());
    this.service = new MemberProvider(reader, writer, null, null, null);
  }

  @Test
  void testSunnyPath() {
    // setup
    MemberID memberID = new MemberID(1L);

    // execute
    Optional<MemberDashboard> y = assertDoesNotThrow(() -> service.findDashboard(memberID)
    );

    // verify
    MockProxy
      .toProxy(reader)
      .assertCallCount(1)
      .assertCall(0, "findMemberDashboard", memberID);
  }
}
