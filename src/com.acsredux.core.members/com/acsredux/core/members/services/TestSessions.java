package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_SESSION_ID;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.SessionID;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSessions {

  private MemberService service;
  private MemberReader reader;
  private MemberWriter writer;

  @BeforeEach
  void setup() {
    this.reader = mock(MemberReader.class);
    this.writer = mock(MemberWriter.class);
    this.service = new MemberProvider(this.reader, this.writer, null, null, null);
  }

  @Test
  void testCreateSessionCallsPort() {
    // execute
    SessionID y = service.createSessionID(TEST_MEMBER_ID);

    // verify
    then(writer).should().writeSessionID(TEST_MEMBER_ID, y);
  }

  @Test
  void testfindSessionCallsPort() {
    // execute
    Optional<Member> y = service.findBySessionID(TEST_SESSION_ID);

    // verify
    then(reader).should().findBySessionID(TEST_SESSION_ID);
  }
}
