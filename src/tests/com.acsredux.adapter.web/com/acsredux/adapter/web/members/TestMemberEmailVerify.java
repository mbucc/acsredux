package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.MockHttpExchange.projectRoot;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.TEST_VERIFICATION_TOKEN;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockMemberService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberEmailVerify {

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
  void testIsEmailVerify() {
    assertFalse(
      handler.dashboardHandler.isEmailVerify(
        new MockHttpExchange("/members/2?token=abc123")
      )
    );
    assertFalse(
      handler.dashboardHandler.isEmailVerify(new MockHttpExchange("/members/2"))
    );
    assertFalse(handler.dashboardHandler.isEmailVerify(new MockHttpExchange("/members")));
  }

  @Test
  void testGetDashboardWithValidToken() {
    // setup
    String url = String.format(
      "/members/%d?token=%s",
      TEST_MEMBER_ID.val(),
      URLEncoder.encode(TEST_VERIFICATION_TOKEN.val(), StandardCharsets.UTF_8)
    );
    var mock = new MockHttpExchange(url, "GET");
    this.mockMemberService.setDashboard(new MemberDashboard(TEST_MEMBER));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
