package com.acsredux.adapter.web.articles;

import static com.acsredux.adapter.web.MockHttpExchange.projectRoot;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.adapter.web.photodiary.PhotoDiaryHandler;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCreateArticleHandler {

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
}
