package com.acsredux.adapter.web;

import static com.acsredux.adapter.web.WebUtil.writeResponse;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

class RootHandler extends BaseHandler {

  private String templateRoot;
  private MustacheFactory mf;

  RootHandler(String templateRoot) {
    this.templateRoot = templateRoot;
    this.mf = new DefaultMustacheFactory(new File(this.templateRoot));
  }

  @Override
  List<Route> getRoutes() {
    return Collections.singletonList(new Route(x -> true, this::renderIndex));
  }

  private void renderIndex(HttpExchange x1, FormData x2) {
    Mustache m = mf.compile("index.html");
    Writer writer = new StringWriter();
    try {
      m.execute(writer, Collections.emptyMap()).flush();
    } catch (Exception e) {
      throw new IllegalStateException(
        "error rendering template index.html with data " + Collections.emptyMap(),
        e
      );
    }
    byte[] body = writer.toString().getBytes();
    writeResponse(x1, body);
  }
}
