package com.acsredux.adapter.web;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;

class RootHandler implements HttpHandler {

  private String templateRoot;
  private MustacheFactory mf;

  RootHandler(String templateRoot) {
    this.templateRoot = templateRoot;
    this.mf = new DefaultMustacheFactory(new File(this.templateRoot));
  }

  @Override
  public void handle(HttpExchange x) {
    try {
      x.getRequestBody().transferTo(OutputStream.nullOutputStream());

      Mustache m = mf.compile("index.html");
      Writer writer = new StringWriter();
      m.execute(writer, Collections.emptyMap()).flush();
      byte[] body = writer.toString().getBytes();

      Headers ys = x.getResponseHeaders();
      ys.set("Content-type", "text/html; charset= UTF-8");
      x.sendResponseHeaders(200, body.length);

      OutputStream os = x.getResponseBody();
      os.write(body);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      x.close();
    }
  }
}
