package com.acsredux.adapter.web.common;

import static com.acsredux.adapter.web.common.WebUtil.readRequestBody;
import static com.acsredux.adapter.web.common.WebUtil.safeDump;
import static java.io.OutputStream.nullOutputStream;

import com.acsredux.core.base.NotFoundException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class BaseHandler implements HttpHandler {

  public record Route(
    Predicate<HttpExchange> matcher,
    BiConsumer<HttpExchange, FormData> handler
  ) {}

  protected abstract List<Route> getRoutes();

  // For tests
  private PrintStream err = System.err;

  void setErr(PrintStream x) {
    this.err = x;
  }

  // Always read the request body to be sure we don't
  // end up with an undrained buffer somewhere.
  private void drainRequestBody(HttpExchange x) {
    try {
      x.getRequestBody().transferTo(nullOutputStream());
    } catch (IOException e) {
      throw new IllegalStateException(
        "error reading request " + "body for " + safeDump(x),
        e
      );
    }
  }

  // Parse request body to form data.
  // If the request is a query or a delete, the data is non-null and empty.
  // It is spec-compliant to ignore the request body for GET or DELETE.
  //   For a good discussion of the GET method and request bodies, see
  // https://stackoverflow.com/a/983458/1789168.
  private FormData read(HttpExchange x) {
    FormData y =
      switch (x.getRequestMethod()) {
        case "PUT", "PATCH", "POST" -> FormData.of(readRequestBody(x));
        case "GET" -> {
          drainRequestBody(x);
          yield FormData.of(x.getRequestURI().getRawQuery());
        }
        default -> {
          drainRequestBody(x);
          yield new FormData();
        }
      };
    y.addPrincipal(x);
    return y;
  }

  private void exceptionCatcher(Runnable x1, HttpExchange x2) {
    try {
      x1.run();
    } catch (Exception e) {
      e.printStackTrace(this.err);
      try {
        Headers ys = x2.getResponseHeaders();
        ys.set("Content-type", "text/html; charset= UTF-8");
        byte[] body = "Internal error.".getBytes();
        int status = 500;
        if (e instanceof NotFoundException) {
          body = "Not found.".getBytes();
          status = 404;
        }
        x2.sendResponseHeaders(status, body.length);
        OutputStream os = x2.getResponseBody();
        os.write(body);
      } catch (IOException ioe) {
        System.err.println("could not send error" + ioe.getMessage());
        e.printStackTrace(this.err);
      }
    } finally {
      x2.close();
    }
  }

  @Override
  public void handle(HttpExchange x) {
    for (Route y : getRoutes()) {
      if (y.matcher.test(x)) {
        exceptionCatcher(() -> y.handler.accept(x, read(x)), x);
        return;
      }
    }
    throw new NotFoundException("No route for " + x.toString());
  }
}
