package com.acsredux.adapter.web.auth;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.SessionID;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieAuthenticator extends Authenticator {

  public static final String COOKIE_NAME = "session_id";
  public static final String COOKIE_FMT =
    COOKIE_NAME + "=%s; max-age=%d; SameSite=Strict; Path=/; HttpOnly";

  private final MemberService memberService;

  public CookieAuthenticator(MemberService x) {
    this.memberService = x;
  }

  static Optional<HttpCookie> findAuthCookie(Headers xs) {
    return xs
      .entrySet()
      .stream()
      .filter(o -> o.getKey().equalsIgnoreCase("cookie"))
      .map(Map.Entry::getValue)
      .flatMap(List::stream)
      .flatMap(o -> Stream.of(o.split(" *; *")))
      .map(HttpCookie::parse)
      .flatMap(List::stream)
      .filter(o -> o.getName().equalsIgnoreCase(COOKIE_NAME))
      .findFirst();
  }

  public Authenticator.Result authenticate(HttpExchange x) {
    String username = findAuthCookie(x.getRequestHeaders())
      .map(HttpCookie::getValue)
      .map(SessionID::new)
      .map(memberService::findBySessionID)
      .flatMap(o -> o)
      .map(Member::fullname)
      .orElseGet(memberService::getAnonymousUsername);
    return new Authenticator.Success(new HttpPrincipal(username, "/"));
  }
}
