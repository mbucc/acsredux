package com.acsredux.adapter.web.articles;

import static com.acsredux.adapter.web.MockHttpExchange.projectRoot;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.photodiary.PhotoDiaryHandler;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCreateContentHandler {

  PhotoDiaryHandler handler;
  MockAdminService mockAdminService;
  MockArticleService mockArticleService;

  @BeforeEach
  void setup() {
    this.mockAdminService = new MockAdminService();
    this.mockArticleService = new MockArticleService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.handler =
      new PhotoDiaryHandler(
        projectRoot() + "/web/template",
        this.mockArticleService,
        this.mockAdminService
      );
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/photo-diary/create", "GET");

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
      "command=create_photo_diary&year=2022&name=foobar"
    );
    mock.setPrincipal(new MemberHttpPrincipal(TEST_MEMBER));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
