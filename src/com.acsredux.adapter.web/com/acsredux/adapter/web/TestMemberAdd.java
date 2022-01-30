package com.acsredux.adapter.web;

import com.acsredux.core.members.entities.*;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.values.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestAddMember {

  String expected(String url) {
    return "";
  }

  MockMemberService mockMemberService;
  MembersHandler handler;

  @BeforeEach
  void setup() {
    this.mockMemberService = new MockMemberService();
    this.handler = new MembersHandler("./web/template", mockMemberService);
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
