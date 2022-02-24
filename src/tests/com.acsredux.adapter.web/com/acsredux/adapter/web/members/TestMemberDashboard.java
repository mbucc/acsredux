package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.MockHttpExchange.projectRoot;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.core.members.values.*;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockMemberService;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberDashboard {

  MockMemberService mockMemberService;
  MockAdminService mockAdminService;
  MembersHandler handler;

  @BeforeEach
  void setup() {
    this.mockMemberService = new MockMemberService();
    this.mockAdminService = new MockAdminService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.handler =
      new MembersHandler(
        projectRoot() + "/web/template",
        mockMemberService,
        mockAdminService
      );
  }

  @Test
  void testIsDashboard() {
    assertTrue(
      handler.dashboardHandler.isGetDashboard(new MockHttpExchange("/members/2"))
    );
    assertFalse(
      handler.dashboardHandler.isGetDashboard(new MockHttpExchange("/members"))
    );
  }

  @Test
  void testMemberID() throws URISyntaxException {
    assertEquals(
      new MemberID(1L),
      handler.dashboardHandler.memberID(new URI("/members/1"))
    );
    assertThrows(
      IllegalStateException.class,
      () -> handler.dashboardHandler.memberID(new URI("/members"))
    );
  }

  @Test
  void testGetDashboard() {
    // setup
    var mock = new MockHttpExchange("/members/2", "GET");
    this.mockMemberService.setDashboard(new MemberDashboard(TEST_MEMBER));
    mock.setPrincipal(new MemberHttpPrincipal(TEST_MEMBER));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
