package com.acsredux.adapter.web.common;

import static java.io.OutputStream.nullOutputStream;

import com.acsredux.adapter.web.auth.ACSHttpPrincipal;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.views.BaseView;
import com.acsredux.core.base.Command;
import com.acsredux.core.base.NotAuthorizedException;
import com.acsredux.core.base.Subject;
import com.acsredux.core.content.commands.AddNote;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class WebUtil {

  public static final String CONTENT_TYPE = "Content-type";

  static Parser parser = Parser.builder().build();

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

  public static Member asMember(HttpPrincipal x) {
    if (x instanceof MemberHttpPrincipal x1) {
      return x1.getMember();
    } else {
      return null;
    }
  }

  public static Subject asSubject(HttpPrincipal x) {
    if (x instanceof ACSHttpPrincipal x1) {
      return new Subject(x1.memberID());
    } else {
      return new Subject(null);
    }
  }

  public static Command form2cmd(HttpPrincipal principal, FormData x) {
    final FormCommand cmd;
    try {
      cmd = FormCommand.valueOf(x.get("command").toUpperCase());
    } catch (Exception e) {
      throw new IllegalStateException(
        "invalid command " + x.get("command") + " in form data " + x
      );
    }
    Subject subject = asSubject(principal);

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
        MultipartFilePart f = x.getUploadedFile();
        Member m = asMember(principal);
        yield new UploadPhoto(
          subject,
          ContentID.parse(x.get("parent")),
          new FileName(f.filename()),
          new BlobBytes(f.val()),
          ImageOrientation.of(x.get("imageOrientation")),
          new ImageDate(Instant.ofEpochSecond(Long.parseLong(x.get("imageDate")))),
          m.tz()
        );
      }
      case ADD_NOTE -> new AddNote(
        subject,
        ContentID.parse(x.get("parent")),
        new BlobBytes(x.get("body").getBytes(StandardCharsets.UTF_8))
      );
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

  public static long pathToID(HttpExchange x, int idx) {
    return pathToID(x.getRequestURI(), idx);
  }

  public static long pathToID(URI x, int idx) {
    var xs = x.toString().split("/");
    if (xs.length < idx + 1) {
      throw new IllegalStateException("invalid URI " + x);
    }
    try {
      return Long.parseLong(xs[idx]);
    } catch (Exception e) {
      String fmt = "can't parse long from path component %d in %s";
      throw new IllegalStateException(String.format(fmt, idx, x));
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
      .orElse("");
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

  public static void notFound(HttpExchange x) {
    returnPlainText(x, 404, "Not found".getBytes());
  }

  public static void ok(HttpExchange x) {
    returnPlainText(x, 200, "OK".getBytes());
  }

  public static void created(HttpExchange x, byte[] body) {
    returnPlainText(x, 201, body);
  }

  public static void notAuthorized(HttpExchange x1, NotAuthorizedException x2) {
    returnPlainText(x1, 401, "Not Authorized".getBytes(), x2, System.err);
  }

  public static void internalError(HttpExchange x1, Exception x2) {
    internalError(x1, x2, System.err);
  }

  public static void internalError(HttpExchange x1, Exception x2, PrintStream x3) {
    returnPlainText(x1, 500, "Internal error".getBytes(), x2, x3);
  }

  public static void clientError(HttpExchange x1, String message) {
    returnPlainText(x1, 400, message.getBytes(), null, System.err);
  }

  static void returnPlainText(
    HttpExchange hex,
    int status,
    byte[] body,
    Exception ex,
    PrintStream err
  ) {
    try {
      Headers ys = hex.getResponseHeaders();
      ys.set("Content-type", "text/plain; charset=UTF-8");
      hex.sendResponseHeaders(status, body.length);
      OutputStream os = hex.getResponseBody();
      os.write(body);
    } catch (Exception e1) {
      err.printf("error sending %d: %s%n", status, e1.getMessage());
    } finally {
      if (status > 299 && ex != null) {
        ex.printStackTrace(err);
      }
    }
  }

  static void returnPlainText(HttpExchange x, int status, byte[] body) {
    returnPlainText(x, status, body, null, System.err);
  }

  public static void renderTemplate(
    MustacheFactory factory,
    String templateName,
    BaseView view,
    HttpExchange x1
  ) {
    Mustache m = factory.compile(templateName);
    Writer writer = new StringWriter();
    try {
      m.execute(writer, view).flush();
    } catch (Exception e) {
      String fmt = "error rendering template %s with data: %s";
      throw new IllegalStateException(String.format(fmt, templateName, view), e);
    }
    byte[] body = writer.toString().getBytes();
    writeResponse(x1, body);
  }

  public static String renderMarkdown(String x) {
    // parse is thread-safe
    Node document = parser.parse(x);
    // This looks thread-safe, but leave here as should be super-fast.
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    return renderer.render(document);
  }
}
