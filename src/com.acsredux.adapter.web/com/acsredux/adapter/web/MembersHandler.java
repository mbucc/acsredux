package com.acsredux.adapter.web;

import static com.acsredux.adapter.web.auth.CookieAuthenticator.COOKIE_FMT;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
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
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

class MembersHandler extends BaseHandler {

  private AdminService adminService;
  private Mustache createTemplate;
  private Mustache dashboardTemplate;
  private SiteInfo siteInfo;

  MembersHandler(
    String templateRoot,
    MemberService memberService,
    AdminService adminService
  ) {
    super(memberService);
    MustacheFactory mf = new DefaultMustacheFactory(new File(templateRoot));
    this.adminService = adminService;
    this.createTemplate = mf.compile("members/create.html");
    this.dashboardTemplate = mf.compile("members/dashboard.html");
  }

  void displayCreateForm(HttpExchange x1, FormData x2) {
    x2.add("isInAlphaTesting", adminService.getSiteInfo().siteStatus().name());
    x2.add(
      "alphaTestMemberLimit",
      String.valueOf(adminService.getSiteInfo().limitOnAlphaCustomers())
    );
    x2.add("memberCount", String.valueOf(memberService.activeMembers()));
    x2.add(
      "suggestionBoxURL",
      this.adminService.getSiteInfo().suggestionBoxURL().toString()
    );
    WebUtil.renderForm(this.createTemplate, x1, x2.asMap());
  }

  Map<String, Object> dashboardView(Member x, FormData x2) {
    x2.add("firstName", x.firstName().val());
    x2.add("lastName", x.lastName().val());
    x2.add("memberSince", x.memberSince());
    x2.add(
      "isWaitingOnEmailVerification",
      x.status() == MemberStatus.NEEDS_EMAIL_VERIFICATION ? "true" : ""
    );
    x2.add("isInAlphaTesting", adminService.getSiteInfo().siteStatus().toString());
    x2.add("isEmailVerified", x.status() == MemberStatus.ACTIVE ? "true" : "");
    x2.add(
      "suggestionBoxURL",
      this.adminService.getSiteInfo().suggestionBoxURL().toString()
    );
    x2.add(
      "alphaTestMemberLimit",
      String.valueOf(adminService.getSiteInfo().limitOnAlphaCustomers())
    );
    x2.add("memberCount", String.valueOf(memberService.activeMembers()));
    x2.add("error", Optional.ofNullable(x2).map(o -> o.get("error")).orElse(""));
    return x2.asMap();
  }

  MemberID verifyToken(MemberID x1, FormData x2) {
    if (x2.get("token") != null) {
      VerifyEmail cmd = new VerifyEmail(new VerificationToken(x2.get("token")));
      try {
        this.memberService.handle(cmd);
      } catch (ValidationException e) {
        x2.add("error", e.getMessage());
      }
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
    Function<Member, Map<String, Object>> toViewData = m -> dashboardView(m, x2);

    // Data pipeline.
    Result
      .ok(x1)
      .map(HttpExchange::getRequestURI)
      .map(this::memberID)
      .map(processTokenIfPresent)
      .map(memberService::getDashboard)
      .map(MemberDashboard::member)
      .map(toViewData)
      .map(toHTML)
      .get();
  }

  SessionID sendResponse(HttpExchange x1, SessionID x2) throws IOException {
    x1.sendResponseHeaders(302, -1);
    return x2;
  }

  void handleCreateFormPost(HttpExchange x1, FormData x2) {
    // Create partials for pipeline.
    UnaryOperator<MemberID> addLocationHeader = id -> {
      Headers ys = x1.getResponseHeaders();
      ys.set("Location", "/members/" + id.val());
      return id;
    };
    Function<MemberID, SessionID> getSessionID = id -> memberService.createSessionID(id);
    UnaryOperator<SessionID> setCookie = id -> {
      Headers ys = x1.getResponseHeaders();
      // We will rely on Apache side-car proxy to set the Secure attribute on
      // the cookie (and generally handle SSL).
      ys.set(
        "Set-cookie",
        String.format(
          COOKIE_FMT,
          id.val(),
          adminService.getSiteInfo().cookieMaxAge().toSeconds()
        )
      );
      return id;
    };

    // Pipeline
    Result<SessionID> result = Result
      .ok(x2)
      .map(WebUtil::form2cmd)
      .map(MemberCommand.class::cast)
      .map(memberService::handle)
      .map(MemberAdded.class::cast)
      .map(MemberAdded::memberID)
      .map(addLocationHeader)
      .map(getSessionID)
      .map(setCookie)
      .mapWrap(id -> sendResponse(x1, id));

    if (result.isErr()) {
      RuntimeException e = result.getError();
      if (e instanceof ValidationException e1) {
        x2.add("error", e1.getMessage());
        WebUtil.renderForm(this.createTemplate, x1, x2.asMap());
      } else {
        throw e;
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
