package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.auth.CookieAuthenticator.COOKIE_FMT;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.MemberService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import java.net.URLEncoder;

public class Util {

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

  public static String dumpReq(HttpExchange x) {
    return String.format("%s %s", x.getRequestMethod(), x.getRequestURI().getPath());
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

  /**
   * Parse entity ID from last component in URL path and return as a long.
   */
  public static long uriToLong(URI x) {
    String[] ys = x.getPath().split("/");
    try {
      return Long.parseLong(ys[ys.length - 1]);
    } catch (Exception e) {
      throw new IllegalStateException("no ID at end of " + x.getPath());
    }
  }

  /**
   * Convert to lower case, replace spaces with underscores, and url encode.
   */
  public static String titleToSlug(String x) {
    try {
      return URLEncoder.encode(x.toLowerCase().replaceAll(" ", "_"), "UTF-8");
    } catch (Exception e) {
      throw new IllegalStateException(String.format("can't convert '%s' to slug", x), e);
    }
  }
}
