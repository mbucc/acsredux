package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.auth.CookieAuthenticator.COOKIE_FMT;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.values.MemberID;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Map;

class Util {

  static void addMenu(Map<String, Object> xs, SiteInfo x1) {
    xs.put(
      "menuitems",
      List.of(
        Map.of("link", "/", "text", "home"),
        Map.of("link", x1.suggestionBoxURL(), "text", "suggestions")
      )
    );
  }

  public static String redirect(HttpExchange x1, String newLocation) {
    Headers ys = x1.getResponseHeaders();
    ys.set("Location", newLocation);
    try {
      x1.sendResponseHeaders(302, 0);
    } catch (Exception e) {
      throw new RuntimeException("redirect to " + newLocation + " failed", e);
    }
    return newLocation;
  }

  static MemberSession createSession(MemberID mid, MemberService x) {
    return new MemberSession(mid, x.createSessionID(mid));
  }

  // Use the Apache side-car proxy to set the Secure attribute on
  // the cookie (and generally handle SSL).
  static MemberSession addCookie(HttpExchange x1, long maxAge, MemberSession x2) {
    Headers ys = x1.getResponseHeaders();
    ys.set("Set-cookie", String.format(COOKIE_FMT, x2.sid().val(), maxAge));
    return x2;
  }
}
