package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.acsredux.core.members.entities.*;
import com.acsredux.core.members.values.*;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberEmailVerify {

  String expected(String url) {
    return "";
  }

  MockMemberService mockMemberService;
  MembersHandler handler;

  @BeforeEach
  void setup() {
    this.mockMemberService = new MockMemberService();
    this.handler = new MembersHandler("./web/template", mockMemberService);
  }

  @Test
  void testIsEmailVerify() throws URISyntaxException {
    assertFalse(handler.isEmailVerify(new MockHttpExchange("/members/2?token=abc123")));
    assertFalse(handler.isEmailVerify(new MockHttpExchange("/members/2")));
    assertFalse(handler.isEmailVerify(new MockHttpExchange("/members")));
  }

  @Test
  void testGetDashboardWithValidToken() throws UnsupportedEncodingException {
    // setup
    String url = String.format(
      "/members/%d?token=%s",
      TEST_MEMBER_ID.val(),
      URLEncoder.encode(TEST_TOKEN.val(), "UTF8")
    );
    var mock = new MockHttpExchange(url, "GET");
    this.mockMemberService.setDashboard(new MemberDashboard(TEST_MEMBER));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
