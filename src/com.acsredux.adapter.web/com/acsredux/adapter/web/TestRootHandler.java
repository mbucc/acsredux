package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;

import com.acsredux.adapter.web.auth.MemberPrincipal;
import org.junit.jupiter.api.Test;

class TestRootHandler {

  String expected(String url) {
    return "";
  }

  @Test
  void testSunnyPath() {
    // setup
    MockMemberService memberService = new MockMemberService();
    var handler = new RootHandler(memberService, "./web/template");
    var mock = new MockHttpExchange("/");

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testMemberLoggedIn() {
    // setup
    MockMemberService memberService = new MockMemberService();
    var handler = new RootHandler(memberService, "./web/template");
    var mock = new MockHttpExchange("/");
    mock.setPrincipal(new MemberPrincipal(TEST_MEMBER));
    mock.setGoldenSuffix("-loggedin");

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
