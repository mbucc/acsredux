package com.acsredux.adapter.web.common;

import static com.acsredux.adapter.web.common.WebUtil.internalError;
import static com.acsredux.adapter.web.common.WebUtil.notFound;

import com.acsredux.core.base.NotFoundException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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

  private void exceptionCatcher(Runnable x1, HttpExchange x2) {
    try {
      x1.run();
    } catch (NotFoundException e) {
      notFound(x2);
    } catch (Exception e) {
      internalError(x2, e, this.err);
    } finally {
      x2.close();
    }
  }

  @Override
  public void handle(HttpExchange x) {
    for (Route y : getRoutes()) {
      final boolean isMatch;
      try {
        isMatch = y.matcher.test(x);
      } catch (Exception e) {
        internalError(x, e);
        return;
      }

      if (isMatch) {
        exceptionCatcher(() -> y.handler.accept(x, FormData.of(x)), x);
        return;
      }
    }
    // Note: any exception thrown here is not be printed by
    // com.sun.net.httpserver.HttpServer.
    notFound(x);
  }
}
