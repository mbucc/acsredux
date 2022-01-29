package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.core.members.events.EmailVerified;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import java.time.Instant;
import java.time.InstantSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestVerifyEmailHandler {

  private VerifyEmailHandler service;
  private MemberReader reader;
  private MemberWriter writer;
  private InstantSource clock;
  private CreatedOn clockTime = new CreatedOn(Instant.now());

  @BeforeEach
  void setup() {
    this.clock = InstantSource.fixed(this.clockTime.val());
    this.reader = mock(MemberReader.class);
    this.writer = mock(MemberWriter.class);
    this.service = new VerifyEmailHandler(reader, writer, this.clock);
  }

  @Test
  void testSunnyPath() {
    // setup
    given(reader.getByToken(TEST_TOKEN)).willReturn(TEST_MEMBER_ID);
    given(reader.getByID(TEST_MEMBER_ID)).willReturn(TEST_MEMBER);
    given(writer.updateStatus(TEST_MEMBER_ID, MemberStatus.ACTIVE))
      .willReturn(TEST_MEMBER_ID);

    // execute
    EmailVerified y = assertDoesNotThrow(() -> service.handle(TEST_VERIFY_EMAIL_CMD));

    // verify
    assertEquals(TEST_MEMBER, y.member());

    then(reader).should().getByToken(TEST_TOKEN);
    then(writer).should().updateStatus(TEST_MEMBER_ID, MemberStatus.ACTIVE);
    then(reader).should().getByID(TEST_MEMBER_ID);

    then(reader).shouldHaveNoMoreInteractions();
    then(writer).shouldHaveNoMoreInteractions();
  }
}
