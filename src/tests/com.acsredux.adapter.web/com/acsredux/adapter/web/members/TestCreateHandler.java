package com.acsredux.adapter.web.members;

import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.projectRoot;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.values.*;
import com.acsredux.lib.testutil.MockAdminService;
import com.acsredux.lib.testutil.MockArticleService;
import com.acsredux.lib.testutil.MockMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCreateHandler {

  MockMemberService mockMemberService;
  MockAdminService mockAdminService;
  MembersHandler handler;

  @BeforeEach
  void setup() {
    this.mockMemberService = new MockMemberService();
    this.mockAdminService = new MockAdminService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.handler =
      new MembersHandler(
        projectRoot() + "/web/template",
        mockMemberService,
        mockAdminService,
        new MockArticleService()
      );
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/members/create", "GET");

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testCreatePostSunny() {
    // setup
    var body =
      "email=t%40t.com&" +
      "firstName=Bill&" +
      "lastName=Russel&" +
      "zip=12345&" +
      "pwd1=aBcd3fgh!&" +
      "pwd2=aBcd3fgh!&" +
      "command=create";
    var mock = new MockHttpExchange("/members/create", "POST", body);
    this.mockMemberService.setEvent(new MemberAdded(null, null, null, new MemberID(1L)));

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }

  @Test
  void testCreatePostError() {
    // setup
    var body =
      "email=t%40t.com&" +
      "firstName=Bill&" +
      "lastName=Russel&" +
      "zip=12345&" +
      "pwd2=aBcd3fgh!&" +
      "command=create";
    var mock = new MockHttpExchange("/members/create", "POST", body);
    mock.setGoldenSuffix("no_password");

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
