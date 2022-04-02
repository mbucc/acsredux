package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.MockHttpExchange.TEST_HTTP_PRINCIPAL;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.projectRoot;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockContentService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDiaryHandler {

  MainHandler handler;
  MockAdminService mockAdminService;
  MockContentService mockArticleService;
  MockMemberService mockMemberService;

  @BeforeEach
  void setup() {
    this.mockAdminService = new MockAdminService();
    this.mockArticleService = new MockContentService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.mockMemberService = new MockMemberService();
    this.handler =
      new MainHandler(
        projectRoot() + "/web/template",
        this.mockArticleService,
        this.mockAdminService,
        this.mockMemberService
      );
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/photo-diary/create", "GET");
    mock.setPrincipal(TEST_HTTP_PRINCIPAL);

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testPostWithError() {
    // setup
    var mock = new MockHttpExchange(
      "/photo-diary/create",
      "POST",
      "command=create_photo_diary&name=foobar"
    );
    mock.setPrincipal(new MemberHttpPrincipal(TEST_MEMBER));
    mock.setGoldenSuffix("no_year");

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testPostSuccess() {
    // setup
    var mock = new MockHttpExchange(
      "/photo-diary/create",
      "POST",
      "command=create_photo_diary&year=2022&filename=foobar"
    );
    mock.setPrincipal(new MemberHttpPrincipal(TEST_MEMBER));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
