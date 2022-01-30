package com.acsredux.adapter.web;

import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.values.*;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import de.perschon.resultflow.Result;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

class MembersHandler extends BaseHandler {

  private MemberService service;
  private Mustache createTemplate;
  private Mustache dashboardTemplate;

  MembersHandler(String templateRoot, MemberService service) {
    MustacheFactory mf = new DefaultMustacheFactory(new File(templateRoot));
    this.service = service;
    this.createTemplate = mf.compile("members/create.html");
    this.dashboardTemplate = mf.compile("members/dashboard.html");
  }

  void displayCreateForm(HttpExchange x1, FormData x2) {
    WebUtil.renderForm(this.createTemplate, x1, x2.asMap());
  }

  Map<String, Object> dashboardView(Member x) {
    return Map.of(
      "firstName",
      x.firstName().val(),
      "lastName",
      x.lastName().val(),
      "memberSince",
      x.memberSince(),
      "isWaitingOnEmailVerification",
      x.status() == MemberStatus.NEEDS_EMAIL_VERIFICATION,
      "isInAlphaTesting",
      true,
      "isEmailVerified",
      x.status() == MemberStatus.ACTIVE
    );
  }

  MemberID verifyToken(MemberID x1, FormData x2) {
    if (x2.get("token") != null) {
      VerifyEmail cmd = new VerifyEmail(new VerificationToken(x2.get("token")));
      this.service.handle(cmd);
    }
    return x1;
  }

  void displayDashboard(HttpExchange x1, FormData x2) {
    // Partials for data pipeline.
    UnaryOperator<MemberID> processTokenIfPresent = id -> verifyToken(id, x2);
    UnaryOperator<Map<String, Object>> toHTML = data -> {
      WebUtil.renderForm(this.dashboardTemplate, x1, data);
      return data;
    };

    // Data pipeline.
    Result
      .ok(x1)
      .map(HttpExchange::getRequestURI)
      .map(this::memberID)
      .map(processTokenIfPresent)
      .map(service::getDashboard)
      .map(MemberDashboard::member)
      .map(this::dashboardView)
      .map(toHTML)
      .get();
  }

  void handleCreateFormPost(HttpExchange x1, FormData x2) {
    Result<String> result = Result
      .ok(x2)
      .map(WebUtil::form2cmd)
      .map(MemberCommand.class::cast)
      .map(service::handle)
      .map(MemberAdded.class::cast)
      .map(MemberAdded::memberID)
      .map(MemberID::val)
      .map(id -> "/members/" + id);

    if (result.isOk()) {
      Headers ys = x1.getResponseHeaders();
      ys.set("Location", result.getValue());
      try {
        x1.sendResponseHeaders(302, -1);
      } catch (Exception e) {
        throw new IllegalStateException("can't set response headers", e);
      }
    } else {
      Exception e = result.getError();
      if (e instanceof ValidationException iae) {
        x2.add("error", iae.getMessage());
        WebUtil.renderForm(this.createTemplate, x1, x2.asMap());
      } else {
        throw new IllegalStateException(e);
      }
    }
  }

  boolean isEmailVerify(HttpExchange x) {
    return x.getRequestURI().getPath().matches("^.*/\\d+?token=");
  }

  boolean isDashboard(HttpExchange x) {
    return x.getRequestURI().getPath().matches("^.*/\\d+$");
  }

  boolean isGetForm(HttpExchange x) {
    return x.getRequestMethod().equalsIgnoreCase("GET");
  }

  boolean isPostForm(HttpExchange x) {
    return x.getRequestMethod().equalsIgnoreCase("POST");
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
  List<Route> getRoutes() {
    return List.of(
      new Route(this::isDashboard, this::displayDashboard),
      new Route(this::isGetForm, this::displayCreateForm),
      new Route(this::isPostForm, this::handleCreateFormPost)
    );
  }
}
