package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.members.Util.addMenu;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.AnonymousPrincipal;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.MemberPrincipal;
import com.acsredux.core.members.values.MemberStatus;
import com.acsredux.core.members.values.VerificationToken;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import de.perschon.resultflow.Result;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.security.auth.Subject;

class Dashboard {

  private final MemberService memberService;
  private final SiteInfo siteInfo;
  private final Mustache template;

  Dashboard(MustacheFactory mf, MemberService x1, SiteInfo x2) {
    this.memberService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("members/dashboard.html");
  }

  void handleGetDashboard(HttpExchange x1, FormData x2) {
    // Partial for data pipeline.
    UnaryOperator<Map<String, Object>> toHTML = data -> {
      WebUtil.renderForm(template, x1, data);
      return data;
    };

    // Data pipeline.
    Result
      .ok(x1)
      .map(HttpExchange::getRequestURI)
      .map(this::memberID)
      .map(memberService::getDashboard)
      .map(MemberDashboard::member)
      .map(mid -> dashboardView(mid, x2))
      .map(toHTML)
      .get();
  }

  void handleGetDashboardWithVerificationToken(HttpExchange x1, FormData x2) {
    // Partial for data pipeline.
    UnaryOperator<Map<String, Object>> toHTML = data -> {
      WebUtil.renderForm(template, x1, data);
      return data;
    };

    // Data pipeline.
    Result
      .ok(x1)
      .map(HttpExchange::getRequestURI)
      .map(this::memberID)
      .map(mid -> verifyToken(x1.getPrincipal(), mid, x2))
      .map(memberService::getDashboard)
      .map(MemberDashboard::member)
      .map(mid -> dashboardView(mid, x2))
      .map(toHTML)
      .get();
  }

  MemberID memberID(URI x) {
    String[] ys = x.getPath().split("/");
    try {
      return new MemberID(Long.valueOf(ys[ys.length - 1]));
    } catch (Exception e) {
      throw new IllegalStateException("no member ID in " + x.getPath());
    }
  }

  Subject subject(HttpPrincipal x) {
    Principal y = new AnonymousPrincipal();
    if (x instanceof MemberHttpPrincipal x1) {
      y = MemberPrincipal.of(x1.getMember());
    }
    return new Subject(true, Set.of(y), Collections.emptySet(), Collections.emptySet());
  }

  MemberID verifyToken(HttpPrincipal x1, MemberID x2, FormData x3) {
    if (x3.get("token") != null) {
      VerifyEmail cmd = new VerifyEmail(
        subject(x1),
        new VerificationToken(x3.get("token"))
      );
      try {
        memberService.handle(cmd);
      } catch (ValidationException e) {
        x3.add("error", e.getMessage());
      }
    }
    return x2;
  }

  Map<String, Object> dashboardView(Member x, FormData x2) {
    Map<String, Object> ys = x2.asMap();
    ys.put("firstName", x.firstName().val());
    ys.put("lastName", x.lastName().val());
    ys.put("memberSince", x.memberSince());
    ys.put(
      "isWaitingOnEmailVerification",
      x.status() == MemberStatus.NEEDS_EMAIL_VERIFICATION
    );
    ys.put("isInAlphaTesting", siteInfo.siteStatus());
    ys.put("isEmailVerified", x.status() == MemberStatus.ACTIVE);
    ys.put("suggestionBoxURL", siteInfo.suggestionBoxURL());
    ys.put("alphaTestMemberLimit", siteInfo.limitOnAlphaCustomers());
    ys.put("memberCount", memberService.activeMembers());
    ys.put("error", Optional.of(x2).map(o -> o.get("error")).orElse(""));
    ys.put("pageTitle", "Member Dashboard");
    addMenu(ys, siteInfo);
    return ys;
  }

  boolean isGetDashboard(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/members/\\d+$")
    );
  }

  boolean isGetDashboardWithVerificationToken(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/members/\\d+?token=")
    );
  }
}
