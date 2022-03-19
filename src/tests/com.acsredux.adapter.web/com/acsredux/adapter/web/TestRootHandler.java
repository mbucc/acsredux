package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.projectRoot;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestRootHandler {

  private RootHandler handler;

  @BeforeEach
  void setup() {
    MockMemberService memberService = new MockMemberService();
    MockAdminService adminService = new MockAdminService();
    handler = new RootHandler(projectRoot() + "/web/template", adminService);
  }

  @Test
  void testSunnyPath() {
    // setup
    var mock = new MockHttpExchange("/");

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testMemberLoggedIn() {
    // setup
    var mock = new MockHttpExchange("/");
    mock.setPrincipal(new MemberHttpPrincipal(TEST_MEMBER));
    mock.setGoldenSuffix("loggedin");

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
