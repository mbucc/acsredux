package com.acsredux.adapter.web;

import com.acsredux.base.Event;
import com.acsredux.base.ValidationException;
import com.acsredux.members.CommandService;
import com.acsredux.members.commands.BaseCommand;
import com.acsredux.members.events.MemberAdded;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.*;
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
import java.util.Optional;

class MembersHandler implements HttpHandler {

  private CommandService commandHandler;
  private Mustache createTemplate;
  private Mustache dashboardTemplate;

  MembersHandler(String templateRoot, CommandService commandHandler) {
    MustacheFactory mf = new DefaultMustacheFactory(new File(templateRoot));
    this.commandHandler = commandHandler;
    this.createTemplate = mf.compile("members/create.html");
    this.dashboardTemplate = mf.compile("members/dashboard.html");
  }

  void displayCreateForm(HttpExchange x) throws IOException {
    x.getRequestBody().transferTo(OutputStream.nullOutputStream());
    Util.renderForm(this.createTemplate, x, Collections.emptyMap());
  }

  void displayDashboard(HttpExchange x) throws IOException, Exception {
    x.getRequestBody().transferTo(OutputStream.nullOutputStream());

    var grabber = new Object() {
      MemberID id;
    };
    Result<Optional<MemberDashboard>> result = Result
      .ok(x)
      .map(HttpExchange::getRequestURI)
      .map(this::memberID)
      .map(o -> {
        grabber.id = o;
        return o;
      })
      .map(FindMemberDashboard::new)
      .map(commandHandler::handle);

    if (result.isOk()) {
      if (result.getValue().isPresent()) {
        Util.renderForm(this.dashboardTemplate, x, result.getValue());
      } else {
        System.out.println("MKB: memberID = " + grabber.id);
        throw new FileNotFoundException("Member not found.");
      }
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
      .map(BaseCommand.class::cast)
      .map(commandHandler::handle);

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
