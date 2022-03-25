package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.MockHttpExchange.TEST_HTTP_PRINCIPAL;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.projectRoot;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockContentService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestUpdateHandler {

  PhotoDiaryHandler handler;
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
      new PhotoDiaryHandler(
        projectRoot() + "/web/template",
        this.mockContentService,
        this.mockAdminService,
        this.mockMemberService
      );
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/photo-diary/123", "GET");
    mock.setPrincipal(TEST_HTTP_PRINCIPAL);
    mockMemberService.setMember(TEST_MEMBER);

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
