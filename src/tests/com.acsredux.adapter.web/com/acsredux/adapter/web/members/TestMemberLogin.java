package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.MockHttpExchange.projectRoot;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.auth.AnonymousHttpPrincipal;
import com.acsredux.core.content.ContentService;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockArticleService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberLogin {

  private MembersHandler handler;

  @BeforeEach
  void setup() {
    MockMemberService memberService = new MockMemberService();
    MockAdminService adminService = new MockAdminService();
    ContentService contentService = new MockArticleService();
    handler =
      new MembersHandler(
        projectRoot() + "/web/template",
        memberService,
        adminService,
        contentService
      );
  }

  @Test
  void testGetMemberLogin() {
    // setup
    var mock = new MockHttpExchange("/members/login");
    mock.setPrincipal(new AnonymousHttpPrincipal());

    // execute
    handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
