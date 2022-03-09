package com.acsredux.adapter.web.common;

import static java.io.OutputStream.nullOutputStream;

import com.acsredux.adapter.web.auth.ACSHttpPrincipal;
import com.acsredux.core.base.Command;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.DiaryName;
import com.acsredux.core.content.values.DiaryYear;
import com.acsredux.core.content.values.FileContent;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.content.values.SectionIndex;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.values.*;
import com.github.mustachejava.Mustache;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.security.auth.Subject;

public class WebUtil {

  public static final String CONTENT_TYPE = "Content-type";

  private WebUtil() {
    throw new UnsupportedOperationException("static only");
  }

  public static Object renderForm(Mustache m, HttpExchange x, Object data) {
    try {
      Writer writer = new StringWriter();
      m.execute(writer, data);
      byte[] body = writer.toString().getBytes();

      Headers ys = x.getResponseHeaders();
      ys.set(CONTENT_TYPE, "text/html; charset= UTF-8");
      x.sendResponseHeaders(200, body.length);

      OutputStream os = x.getResponseBody();
      os.write(body);
      return data;
    } catch (Exception e) {
      throw new IllegalStateException("can't render form with data=" + data, e);
    }
  }

  public static byte[] readRequestBody(HttpExchange x) {
    try {
      return x.getRequestBody().readAllBytes();
    } catch (IOException e) {
      throw new IllegalStateException("error reading request", e);
    }
  }

  public static String toUTF8String(byte[] xs) {
    return new String(xs, StandardCharsets.UTF_8);
  }

  public static Command form2cmd(ACSHttpPrincipal principal, FormData x) {
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
      Set.of(principal.asMemberPrincipal()),
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
      case UPLOAD_PHOTO -> {
        FilePart f = x.getUploadedFiles().get(0);
        yield new UploadPhoto(
          subject,
          ContentID.parse(x.get("contentID")),
          SectionIndex.parse(x.get("sectionIndex")),
          new FileName(f.filename()),
          new FileContent(f.val())
        );
      }
    };
  }

  // Don't leak sensitive data.
  public static void writeResponse(HttpExchange x1, byte[] body) {
    try {
      Headers ys = x1.getResponseHeaders();
      ys.set(CONTENT_TYPE, "text/html; charset= UTF-8");
      x1.sendResponseHeaders(200, body.length);
      OutputStream os = x1.getResponseBody();
      os.write(body);
    } catch (IOException e) {
      throw new IllegalStateException("can't write response", e);
    }
  }

  public static String dumpRequest(HttpExchange x) {
    StringBuilder buf = new StringBuilder();
    buf.append(x.getRequestMethod());
    buf.append(" ");
    buf.append(x.getRequestURI().toString());
    buf.append("\n\n");
    if (x.getRequestHeaders().isEmpty()) {
      buf.append("No HTTP headers");
    } else {
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
        buf.append("\n");
      }
    }
    return buf.toString();
  }

  public static long parseLongFromURIPathComponent(URI x, int i) {
    var xs = x.toString().split("/");
    if (xs.length < i + 1) {
      throw new IllegalStateException("invalid URI " + x);
    }
    try {
      return Long.parseLong(xs[i]);
    } catch (Exception e) {
      String fmt = "can't parse long from path component %d in %s";
      throw new IllegalStateException(String.format(fmt, i, x));
    }
  }

  static void drainRequestBody(HttpExchange x) {
    try {
      x.getRequestBody().transferTo(nullOutputStream());
    } catch (IOException e) {
      throw new IllegalStateException(
        "error reading request " + "body for " + dumpRequest(x),
        e
      );
    }
  }

  static String getContentTypeFromString(String x) {
    return Optional
      .ofNullable(x)
      .map(String::trim)
      .filter(o -> o.toLowerCase().contains("content-type:"))
      .map(o -> o.replaceAll("(?i).*Content-Type: *", ""))
      .map(o -> o.replaceAll(" *;.*", ""))
      .orElseThrow(() -> new NoSuchElementException("no content type in '" + x + "'"));
  }

  static String getContentType(HttpExchange x) {
    Objects.requireNonNull(x);
    return Optional
      .of(x)
      .map(HttpExchange::getRequestHeaders)
      .map(o -> o.get(CONTENT_TYPE))
      .filter(o -> !o.isEmpty())
      .map(o -> o.get(0))
      .orElseThrow(() ->
        new NoSuchElementException(
          "no " + CONTENT_TYPE + " header in:\n" + dumpRequest(x)
        )
      );
  }

  static String getHeaderParameter(String header, String parameterName) {
    var marker = (parameterName + "=").toLowerCase();
    return Stream
      .of(header.split(";"))
      .map(String::trim)
      .filter(o -> o.toLowerCase().startsWith(marker))
      .map(o -> o.split("=")[1])
      .findFirst()
      .map(o -> o.replaceAll("^\"|\"$", ""))
      .orElseThrow(() ->
        new NoSuchElementException("no " + parameterName + " in header '" + header + "'")
      );
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

  public static void clientError(HttpExchange x1, int status, String message) {
    try {
      setPlainTextResponse(x1);
      writeBody(x1, message.getBytes(), status);
    } catch (Exception e1) {
      System.err.printf("error sending client error: %s%n", e1.getMessage());
    }
  }
}
