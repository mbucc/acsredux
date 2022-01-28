package com.acsredux.adapter.web;

import com.acsredux.core.base.Command;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.values.*;
import com.github.mustachejava.Mustache;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

class WebUtil {

  private WebUtil() {
    throw new UnsupportedOperationException("static only");
  }

  static void renderForm(Mustache m, HttpExchange x, Object data) {
    try {
      Writer writer = new StringWriter();
      m.execute(writer, data);
      byte[] body = writer.toString().getBytes();

      Headers ys = x.getResponseHeaders();
      ys.set("Content-type", "text/html; charset= UTF-8");
      x.sendResponseHeaders(200, body.length);

      OutputStream os = x.getResponseBody();
      os.write(body);
    } catch (Exception e) {
      throw new IllegalStateException("can't render form with data=" + data, e);
    }
  }

  static String readRequestBody(HttpExchange x) {
    final byte[] y;
    try {
      y = x.getRequestBody().readAllBytes();
    } catch (IOException e) {
      throw new IllegalStateException("error reading request", e);
    }
    return toUTF8String(y);
  }

  static FormData parseFormData(String encodedRequestBody) {
    FormData y = new FormData();
    if (encodedRequestBody == null || encodedRequestBody.isBlank()) {
      return y;
    }
    try {
      for (String pair : encodedRequestBody.split("&")) {
        String[] pieces = pair.split("=");
        String k = URLDecoder.decode(pieces[0], StandardCharsets.UTF_8);
        String v = URLDecoder.decode(pieces[1], StandardCharsets.UTF_8);
        y.add(k, v);
      }
    } catch (Exception e) {
      // Some form posted invalid form data
      throw new IllegalStateException("invalid form post data: " + encodedRequestBody, e);
    }
    return y;
  }

  static String toUTF8String(byte[] xs) {
    return new String(xs, StandardCharsets.UTF_8);
  }

  static Command form2cmd(FormData x) {
    final FormCommand cmd;
    try {
      cmd = FormCommand.valueOf(x.get("command").toUpperCase());
    } catch (Exception e) {
      throw new IllegalStateException(
        "invalid command " + x.get("command") + " in form data " + x
      );
    }
    return switch (cmd) {
      case CREATE_MEMBER -> new AddMember(
        new FirstName(x.get("firstName")),
        new LastName(x.get("lastName")),
        new Email(x.get("email")),
        new ClearTextPassword(x.get("pwd1")),
        new ClearTextPassword(x.get("pwd2")),
        new ZipCode(x.get("zip"))
      );
    };
  }

  // Don't leak sensitive data.
  static void writeResponse(HttpExchange x1, byte[] body) {
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

  static String safeDump(HttpExchange x) {
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
          buf.append(y.getValue().toString());
        }
      } else {
        buf.append("null");
      }
    }
    return buf.toString();
  }
}
