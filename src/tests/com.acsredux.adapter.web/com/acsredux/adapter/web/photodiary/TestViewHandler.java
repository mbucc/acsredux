package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.MockHttpExchange.TEST_HTTP_PRINCIPAL;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER2;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.projectRoot;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockContentService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TestViewHandler {

  MainHandler handler;
  MockAdminService mockAdminService;
  MockContentService mockContentService;
  MockMemberService mockMemberService;

  @BeforeEach
  void setup() {
    this.mockAdminService = new MockAdminService();
    this.mockContentService = new MockContentService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.mockMemberService = new MockMemberService();
    this.handler =
      new MainHandler(
        projectRoot() + "/web/template",
        this.mockContentService,
        this.mockAdminService,
        this.mockMemberService
      );
  }

  @Test
  @Disabled("work in progress ...")
  void testGetWhenEditor() {
    // setup
    var mock = new MockHttpExchange("/photo-diary/123", "GET");
    mock.setPrincipal(TEST_HTTP_PRINCIPAL);
    mockMemberService.setMember(TEST_HTTP_PRINCIPAL.getMember());

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testGetWhenNotEditor() {
    // setup
    var mock = new MockHttpExchange("/photo-diary/123", "GET");
    mock.setPrincipal(new MemberHttpPrincipal(TEST_MEMBER2));
    mockMemberService.setMember(TEST_MEMBER);
    mock.setGoldenSuffix("not-editor");

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
