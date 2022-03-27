package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.MockHttpExchange.TEST_HTTP_PRINCIPAL;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.projectRoot;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.core.members.MemberService;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockContentService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestUploadPhotoHandler {

  PhotoDiaryHandler handler;
  MockAdminService mockAdminService;
  MockContentService mockArticleService;
  MemberService memberService;

  @BeforeEach
  void setup() {
    this.mockAdminService = new MockAdminService();
    this.mockArticleService = new MockContentService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.memberService = new MockMemberService();
    this.handler =
      new PhotoDiaryHandler(
        projectRoot() + "/web/template",
        this.mockArticleService,
        this.mockAdminService,
        this.memberService
      );
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/photo-diary/123/add-image", "GET");
    mock.setPrincipal(TEST_HTTP_PRINCIPAL);

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
