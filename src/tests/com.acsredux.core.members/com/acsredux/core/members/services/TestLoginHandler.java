package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.events.MemberLoggedIn;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.LoginTime;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLoginHandler {

  private final CreatedOn clockTime = new CreatedOn(Instant.now());
  private LoginHandler service;
  private MemberReader reader;
  private MemberWriter writer;

  @BeforeEach
  void setup() {
    reader = (MemberReader) MockProxy.of(new MockMemberReader());
    writer = (MemberWriter) MockProxy.of(new MockMemberWriter());
    InstantSource clock = InstantSource.fixed(this.clockTime.val());
    this.service = new LoginHandler(reader, writer, clock);
  }

  @Test
  void testSunnyPath() {
    // execute
    MemberLoggedIn y = assertDoesNotThrow(() -> service.handle(TEST_LOGIN_MEMBER_CMD));

    // verify
    assertNotNull(y);
    assertEquals(TEST_MEMBER, y.member());
    assertEquals(TEST_LOGIN_MEMBER_CMD, y.cmd());
    assertEquals(this.clockTime, y.on());
    LoginTime loginTime = LoginTime.of(clockTime.val());
    MockProxy
      .toProxy(writer)
      .assertCallCount(1)
      .assertCall(0, "setLastLogin", TEST_MEMBER_ID, loginTime);
    MockProxy.toProxy(reader).assertCallCount(1).assertCall(0, "getByEmail", TEST_EMAIL);
  }

  @Test
  void testInvalidPassword() {
    // setup
    LoginMember cmd = new LoginMember(
      TEST_EMAIL,
      new ClearTextPassword("aaaabbbb".toCharArray())
    );
    // execute
    AuthenticationException y = assertThrows(
      AuthenticationException.class,
      () -> service.handle(cmd)
    );

    // verify
    assertNotNull(y);
    assertEquals("Invalid password", y.getMessage());
  }
}
