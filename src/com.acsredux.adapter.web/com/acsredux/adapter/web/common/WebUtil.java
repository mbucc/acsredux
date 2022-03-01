package com.acsredux.adapter.web.common;

import com.acsredux.core.base.Command;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.values.DiaryName;
import com.acsredux.core.content.values.DiaryYear;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.values.*;
import com.github.mustachejava.Mustache;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

public class WebUtil {

  private WebUtil() {
    throw new UnsupportedOperationException("static only");
  }

  public static Object renderForm(Mustache m, HttpExchange x, Object data) {
    try {
      Writer writer = new StringWriter();
      m.execute(writer, data);
      byte[] body = writer.toString().getBytes();

      Headers ys = x.getResponseHeaders();
      ys.set("Content-type", "text/html; charset= UTF-8");
      x.sendResponseHeaders(200, body.length);

      OutputStream os = x.getResponseBody();
      os.write(body);
      return data;
    } catch (Exception e) {
      throw new IllegalStateException("can't render form with data=" + data, e);
    }
  }

  public static String readRequestBody(HttpExchange x) {
    final byte[] y;
    try {
      y = x.getRequestBody().readAllBytes();
    } catch (IOException e) {
      throw new IllegalStateException("error reading request", e);
    }
    return toUTF8String(y);
  }

  public static String toUTF8String(byte[] xs) {
    return new String(xs, StandardCharsets.UTF_8);
  }

  public static Command form2cmd(Principal principal, FormData x) {
    final FormCommand cmd;
    try {
      cmd = FormCommand.valueOf(x.get("command").toUpperCase());
    } catch (Exception e) {
      throw new IllegalStateException(
        "invalid command " + x.get("command") + " in form data " + x
      );
    }
    Subject subject = new Subject(
      true,
      Set.of(principal),
      Collections.emptySet(),
      Collections.emptySet()
    );
    return switch (cmd) {
      case CREATE -> new CreateMember(
        subject,
        new FirstName(x.get("firstName")),
        new LastName(x.get("lastName")),
        new Email(x.get("email")),
        ClearTextPassword.of(x.get("pwd1")),
        ClearTextPassword.of(x.get("pwd2")),
        new ZipCode(x.get("zip"))
      );
      case LOGIN -> new LoginMember(
        subject,
        new Email(x.get("email")),
        ClearTextPassword.of(x.get("pwd"))
      );
      case CREATE_PHOTO_DIARY -> new CreatePhotoDiary(
        subject,
        DiaryYear.parse(x.get("year")),
        new DiaryName(x.get("name"))
      );
    };
  }

  // Don't leak sensitive data.
  public static void writeResponse(HttpExchange x1, byte[] body) {
    try {
      Headers ys = x1.getResponseHeaders();
      ys.set("Content-type", "text/html; charset= UTF-8");
      x1.sendResponseHeaders(200, body.length);
      OutputStream os = x1.getResponseBody();
      os.write(body);
    } catch (IOException e) {
      throw new IllegalStateException("can't write response", e);
    }
  }

  public static String safeDump(HttpExchange x) {
    StringBuilder buf = new StringBuilder();
    buf.append(x.getRequestMethod());
    buf.append(" ");
    buf.append(x.getRequestURI().toString());
    buf.append("\n\n");
    for (Map.Entry<String, List<String>> y : x.getRequestHeaders().entrySet()) {
      buf.append(y.getKey());
      buf.append(": ");
      if (y.getValue() != null) {
        if (y.getValue().size() == 1) {
          buf.append(y.getValue().get(0));
        } else {
          buf.append(y.getValue());
        }
      } else {
        buf.append("null");
      }
    }
    return buf.toString();
  }
}
