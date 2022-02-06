package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;

import com.acsredux.adapter.web.auth.MemberPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestRootHandler {

  private MockMemberService memberService;
  private MockAdminService adminService;
  private RootHandler handler;

  @BeforeEach
  void setup() {
    memberService = new MockMemberService();
    adminService = new MockAdminService();
    handler = new RootHandler(memberService, adminService, "./web/template");
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
    mock.setPrincipal(new MemberPrincipal(TEST_MEMBER));
    mock.setGoldenSuffix("-loggedin");

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
