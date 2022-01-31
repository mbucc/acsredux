package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;

import com.acsredux.core.members.entities.*;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.values.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberAdd {

  String expected(String url) {
    return "";
  }

  MockMemberService mockMemberService;
  MockAdminService mockAdminService;
  MembersHandler handler;

  @BeforeEach
  void setup() {
    this.mockMemberService = new MockMemberService();
    this.mockAdminService = new MockAdminService();
    this.mockAdminService.setSiteInfo(TEST_SITE_INFO);
    this.handler =
      new MembersHandler("./web/template", mockMemberService, mockAdminService);
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/members/create.html", "GET");

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
      "command=create_member";
    var mock = new MockHttpExchange("/members/create.html", "POST", body);
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
      "command=create_member";
    var mock = new MockHttpExchange("/members/create.html.no_password", "POST", body);

    // execute
    this.handler.handle(mock);

    // verify
    mock.goldTest();
  }
}
