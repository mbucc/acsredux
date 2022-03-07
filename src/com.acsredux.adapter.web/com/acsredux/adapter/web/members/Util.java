package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.auth.CookieAuthenticator.COOKIE_FMT;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.values.MemberID;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

  private static void setPlainTextResponse(HttpExchange x) {
    Headers ys = x.getResponseHeaders();
    ys.set("Content-type", "text/plain; charset= UTF-8");
  }

  private static void writeBody(HttpExchange x, byte[] body, int status)
    throws IOException {
    x.sendResponseHeaders(status, body.length);
    OutputStream os = x.getResponseBody();
    os.write(body);
  }

  public static void notFound(HttpExchange x) {
    byte[] body = "Not found.".getBytes();
    int status = 404;
    try {
      setPlainTextResponse(x);
      writeBody(x, body, status);
    } catch (IOException e) {
      // Not much we can do at this point.
      System.err.println("error sending 404");
      e.printStackTrace(System.err);
    }
  }

  public static void internalError(HttpExchange x1, Exception x2) {
    internalError(x1, x2, System.err);
  }

  public static void internalError(HttpExchange x1, Exception x2, PrintStream x3) {
    byte[] body = "Internal error.".getBytes();
    int status = 500;
    try {
      setPlainTextResponse(x1);
      writeBody(x1, body, status);
    } catch (Exception e1) {
      System.err.printf("error sending 500: %s%n", e1.getMessage());
    } finally {
      x2.printStackTrace(x3);
    }
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
