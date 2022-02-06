package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.MockHttpExchange.projectRoot;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.auth.MemberPrincipal;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberLogout {

  private MembersHandler handler;

  @BeforeEach
  void setup() {
    MockMemberService memberService = new MockMemberService();
    MockAdminService adminService = new MockAdminService();
    handler =
      new MembersHandler(projectRoot() + "/web/template", memberService, adminService);
  }

  @Test
  void testMemberLogout() {
    // setup
    var mock = new MockHttpExchange("/members/logout");
    mock.setPrincipal(new MemberPrincipal(TEST_MEMBER));

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
