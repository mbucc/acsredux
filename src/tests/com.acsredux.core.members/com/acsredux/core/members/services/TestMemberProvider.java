package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.entities.PublicMember;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.lib.testutil.MockProxy;
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
    // execut
    PublicMember y = assertDoesNotThrow(() -> service.getPublicByID(TEST_MEMBER_ID));

    // verify
    MockProxy.toProxy(reader).assertCallCount(1).assertCall(0, "getByID", TEST_MEMBER_ID);
  }
}
