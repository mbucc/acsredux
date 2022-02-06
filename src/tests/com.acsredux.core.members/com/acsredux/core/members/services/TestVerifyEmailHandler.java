package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.core.members.events.EmailVerified;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestVerifyEmailHandler {

  private final CreatedOn clockTime = new CreatedOn(Instant.now());
  private VerifyEmailHandler service;
  private MemberWriter writer;
  private MemberReader reader;

  @BeforeEach
  void setup() {
    InstantSource clock = InstantSource.fixed(this.clockTime.val());
    reader = (MemberReader) MockProxy.of(new MockMemberReader());
    writer = (MemberWriter) MockProxy.of(new MockMemberWriter());
    this.service = new VerifyEmailHandler(reader, writer, clock);
  }

  @Test
  void testSunnyPath() {
    // execute
    EmailVerified y = assertDoesNotThrow(() -> service.handle(TEST_VERIFY_EMAIL_CMD));

    // verify
    assertEquals(TEST_MEMBER, y.member());
    MockProxy
      .toProxy(writer)
      .assertCallCount(1)
      .assertCall(0, "updateStatus", TEST_MEMBER_ID, TEST_MEMBER_STATUS);
    MockProxy
      .toProxy(reader)
      .assertCallCount(2)
      .assertCall(0, "getByToken", TEST_VERIFICATION_TOKEN)
      .assertCall(1, "getByID", TEST_MEMBER_ID);
  }
}
