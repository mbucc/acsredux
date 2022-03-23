package com.acsredux.adapter.web.members;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.DashboardView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.base.Subject;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.values.VerificationToken;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import de.perschon.resultflow.Result;

class DashboardHandler {

  private final MemberService memberService;
  private final ContentService contentService;
  private final SiteInfo siteInfo;
  private final Mustache template;

  DashboardHandler(MustacheFactory mf, MemberService x1, SiteInfo x2, ContentService x3) {
    this.memberService = x1;
    this.siteInfo = x2;
    this.contentService = x3;
    this.template = mf.compile("members/dashboard.html");
  }

  void handleGetDashboard(HttpExchange x1, FormData x2) {
    Result
      .ok(new DashboardView(x1, x2, siteInfo, "Member Dashboard"))
      .map(o -> o.lookupMemberInfo(memberService))
      .map(o -> o.lookupMemberArticles(contentService))
      .map(o -> WebUtil.renderForm(template, x1, o))
      .get();
  }

  Subject subject(HttpPrincipal x) {
    if (x instanceof MemberHttpPrincipal x1) {
      return new Subject(x1.getMember().id());
    }
    return new Subject(null);
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
