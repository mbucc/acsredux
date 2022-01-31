package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.core.members.entities.*;
import com.acsredux.core.members.values.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberDashboard {

  String expected(String url) {
    return "";
  }

  MockMemberService mockMemberService;
  MockAdminService mockAdminService;
  MembersHandler handler;

  @BeforeEach
  void setup() {
    this.mockMemberService = new MockMemberService();
    this.mockAdminService = new MockAdminService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.handler =
      new MembersHandler("./web/template", mockMemberService, mockAdminService);
  }

  @Test
  void testIsDashboard() throws URISyntaxException {
    assertTrue(handler.isDashboard(new MockHttpExchange("/members/2")));
    assertFalse(handler.isDashboard(new MockHttpExchange("/members")));
  }

  @Test
  void testMemberID() throws URISyntaxException {
    assertEquals(new MemberID(1L), handler.memberID(new URI("/members/1")));
    assertThrows(
      IllegalStateException.class,
      () -> handler.memberID(new URI("/members"))
    );
  }

  @Test
  void testGetDashboard() {
    // setup
    var mock = new MockHttpExchange("/members/2", "GET");
    this.mockMemberService.setDashboard(new MemberDashboard(TEST_MEMBER));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
