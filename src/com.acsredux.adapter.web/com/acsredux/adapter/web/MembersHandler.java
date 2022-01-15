package com.acsredux.adapter.web;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.values.*;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.perschon.resultflow.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

class MembersHandler implements HttpHandler {

  private MemberService service;
  private Mustache createTemplate;
  private Mustache dashboardTemplate;

  MembersHandler(String templateRoot, MemberService service) {
    MustacheFactory mf = new DefaultMustacheFactory(new File(templateRoot));
    this.service = service;
    this.createTemplate = mf.compile("members/create.html");
    this.dashboardTemplate = mf.compile("members/dashboard.html");
  }

  void displayCreateForm(HttpExchange x) throws IOException {
    x.getRequestBody().transferTo(OutputStream.nullOutputStream());
    Util.renderForm(this.createTemplate, x, Collections.emptyMap());
  }

  void displayDashboard(HttpExchange x) throws IOException, Exception {
    x.getRequestBody().transferTo(OutputStream.nullOutputStream());

    Result<MemberDashboard> result = Result
      .ok(x)
      .map(HttpExchange::getRequestURI)
      .map(this::memberID)
      .map(service::getDashboard);

    if (result.isOk()) {
      Member y = result.getValue().member();
      Map<String, Object> data = Map.of(
        "firstName",
        y.first().val(),
        "lastName",
        y.last().val(),
        "memberSince",
        y.memberSince()
      );
      Util.renderForm(this.dashboardTemplate, x, data);
    } else {
      throw result.getError();
    }
  }

  void handleCreateFormPost(HttpExchange x) throws Exception {
    var grabber = new Object() {
      FormData fd;
    };
    Result<Event> result = Result
      .ok(x)
      .map(HttpExchange::getRequestBody)
      .map(Result.uncheck(InputStream::readAllBytes))
      .map(Util::toUTF8String)
      .map(Util::parseFormData)
      .map(o -> {
        grabber.fd = o;
        return o;
      })
      .map(Util::form2cmd)
      .map(MemberCommand.class::cast)
      .map(service::handle);

    if (result.isOk()) {
      Headers ys = x.getResponseHeaders();
      MemberAdded y1 = (MemberAdded) result.getValue();
      ys.set("Location", "/members/" + y1.memberID().val());
      x.sendResponseHeaders(302, -1);
    } else {
      Exception e = result.getError();
      if (e instanceof ValidationException iae) {
        grabber.fd.add("error", iae.getMessage());
        Util.renderForm(this.createTemplate, x, grabber.fd.asMap());
      } else {
        throw e;
      }
    }
  }

  boolean isDashboard(URI x) {
    return x.getPath().matches("^.*/\\d+$");
  }

  MemberID memberID(URI x) {
    String[] ys = x.getPath().split("/");
    try {
      return new MemberID(Long.valueOf(ys[ys.length - 1]));
    } catch (Exception e) {
      throw new IllegalStateException("no member ID in " + x.getPath());
    }
  }

  @Override
  public void handle(HttpExchange x) {
    try {
      URI uri = x.getRequestURI();
      String m = x.getRequestMethod();
      if (isDashboard(uri)) {
        displayDashboard(x);
      } else {
        switch (m) {
          case "GET":
            displayCreateForm(x);
            break;
          case "POST":
            handleCreateFormPost(x);
            break;
          default:
            throw new UnsupportedOperationException(m + " is not supported");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      try {
        if (e instanceof FileNotFoundException fnfe) {
          Headers ys = x.getResponseHeaders();
          ys.set("Content-type", "text/html; charset= UTF-8");
          byte[] body = "Not found.".getBytes();
          x.sendResponseHeaders(404, body.length);
          OutputStream os = x.getResponseBody();
          os.write(body);
        } else {
          Headers ys = x.getResponseHeaders();
          ys.set("Content-type", "text/html; charset= UTF-8");
          byte[] body = "Internal error.".getBytes();
          x.sendResponseHeaders(500, body.length);
          OutputStream os = x.getResponseBody();
          os.write(body);
        }
      } catch (IOException ioe) {
        System.err.println("could not send 500 response" + ioe.getMessage());
        e.printStackTrace();
      }
    } finally {
      x.close();
    }
  }
}
