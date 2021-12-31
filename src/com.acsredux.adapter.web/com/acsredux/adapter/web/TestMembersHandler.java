package com.acsredux.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.core.members.entities.*;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.values.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MembersHandlerTest {

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
  void testIsDashboard() throws URISyntaxException {
    assertTrue(handler.isDashboard(new URI("/members/2")));
    assertFalse(handler.isDashboard(new URI("/members")));
  }

  @Test
  void testMemberID() throws URISyntaxException {
    assertEquals(new MemberID(1L), handler.memberID(new URI("/members/1")));
    assertThrows(
      IllegalStateException.class,
      () -> handler.memberID(new URI("/members"))
    );
  }

  @Test
  void testGet() {
    // setup
    var mock = new MockHttpExchange("/members/create.html", "GET");

    // execute
    this.handler.handle(mock);

    // verify
    mock.assertResponseIsCorrect();
  }

  @Test
  void testCreatePostSunny() {
    // setup
    var body = "email=t%40t.com&pwd1=aBcd3fgh!&pwd2=aBcd3fgh!&command=create_member";
    var mock = new MockHttpExchange("/members/create.html", "POST", body);
    this.mockMemberService.setEvent(new MemberAdded(null, null, null, new MemberID(1L)));

    // execute
    this.handler.handle(mock);

    // verify
    mock.assertResponseIsCorrect();
  }

  @Test
  void testCreatePostError() {
    // setup
    var body = "email=t%40t.com&command=create_member";
    var mock = new MockHttpExchange("/members/create.html.no_password", "POST", body);

    // execute
    this.handler.handle(mock);

    // verify
    mock.assertResponseIsCorrect();
  }

  @Test
  void testGetDashboard() {
    // setup
    var mock = new MockHttpExchange("/members/2", "GET");
    this.mockMemberService.setDashboard(
        new MemberDashboard(
          new Member(
            new MemberID(2L),
            new Email("t@t.com"),
            new FirstName("first"),
            new LastName("last")
          )
        )
      );

    // execute
    this.handler.handle(mock);

    // verify
    mock.assertResponseIsCorrect();
  }
}