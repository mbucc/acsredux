package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.members.Util.addMenu;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.MemberStatus;
import com.acsredux.core.members.values.VerificationToken;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import de.perschon.resultflow.Result;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

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
      .map(mid -> verifyToken(mid, x2))
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

  MemberID verifyToken(MemberID x1, FormData x2) {
    if (x2.get("token") != null) {
      VerifyEmail cmd = new VerifyEmail(new VerificationToken(x2.get("token")));
      try {
        memberService.handle(cmd);
      } catch (ValidationException e) {
        x2.add("error", e.getMessage());
      }
    }
    return x1;
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
    return x.getRequestURI().getPath().matches("/members/\\d+$");
  }

  boolean isEmailVerify(HttpExchange x) {
    return x.getRequestURI().getPath().matches("/members/\\d+?token=");
  }
}
