package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberProvider {

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
  void testSunnyPath() {
    // setup
    MemberID memberID = new MemberID(1L);
    given(reader.findMemberDashboard(memberID)).willReturn(Optional.empty());

    // execute
    Optional<MemberDashboard> y = assertDoesNotThrow(() -> service.findDashboard(memberID)
    );

    // verify
    then(reader).should().findMemberDashboard(memberID);
  }

  @Test
  void testCreateSessionCallsWriterPort() {
    // execute
    SessionID y = service.createSessionID(TEST_MEMBER_ID);

    // verify
    then(writer).should().writeSessionID(TEST_MEMBER_ID, y);
  }
}
