package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.SessionID;
import com.acsredux.lib.testutil.MockProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSessions {

  private MemberService service;

  @BeforeEach
  void setup() {
    MemberReader reader = (MemberReader) MockProxy.of(new MockMemberReader());
    MemberWriter writer = (MemberWriter) MockProxy.of(new MockMemberWriter());
    this.service = new MemberServiceProvider(reader, writer, null, null, null);
  }

  @Test
  void testCreateSessionCallsPort() {
    // execute
    SessionID y = service.createSessionID(TEST_MEMBER_ID);
    // verify
    //    then(writer).should().writeSessionID(TEST_MEMBER_ID, y);
  }

  @Test
  void testfindSessionCallsPort() {
    // execute
    //    Optional<Member> y = service.findBySessionID(TEST_SESSION_ID);

    // verify
    //    then(reader).should().findBySessionID(TEST_SESSION_ID);
  }
}
