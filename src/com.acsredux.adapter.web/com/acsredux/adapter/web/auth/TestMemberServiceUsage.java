package com.acsredux.adapter.web.auth;

import static com.acsredux.adapter.web.auth.CookieAuthenticator.COOKIE_FMT;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_SESSION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.MockMemberService;
import com.sun.net.httpserver.Authenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberServiceUsage {

  private CookieAuthenticator cookieAuthenticator;
  private MockMemberService mockMemberService = new MockMemberService();
  private MockHttpExchange mockHttpExchange = new MockHttpExchange("/");

  @BeforeEach
  void setup() {
    this.cookieAuthenticator = new CookieAuthenticator(mockMemberService);
  }

  @Test
  void testPrincipalFromMemberService() {
    // setup
    mockMemberService.setMember(TEST_MEMBER);
    mockHttpExchange
      .getRequestHeaders()
      .add("Cookie", String.format(COOKIE_FMT, TEST_SESSION_ID, 60));

    // execute
    Authenticator.Result y = cookieAuthenticator.authenticate(mockHttpExchange);

    // verify
    if (y instanceof Authenticator.Success y1) {
      assertEquals(TEST_MEMBER.fullname(), y1.getPrincipal().getUsername());
    } else {
      fail("return value not an instance of Authenticator.Success: " + y);
    }
  }

  @Test
  void testAnonymousUsernameComesFromMemberService() {
    // setup
    mockMemberService.setAnonymousUsername("foobar");

    // execute
    Authenticator.Result y = cookieAuthenticator.authenticate(mockHttpExchange);

    // verify
    if (y instanceof Authenticator.Success y1) {
      assertEquals("foobar", y1.getPrincipal().getUsername());
    } else {
      fail("return value not an instance of Authenticator.Success: " + y);
    }
  }
}
