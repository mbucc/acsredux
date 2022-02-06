package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.members.Util.addCookie;
import static com.acsredux.adapter.web.members.Util.addMenu;
import static com.acsredux.adapter.web.members.Util.createSession;
import static com.acsredux.adapter.web.members.Util.redirect;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberLoggedIn;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import de.perschon.resultflow.Result;
import java.util.Map;

class Login {

  private final MemberService memberService;
  private final SiteInfo siteInfo;
  private final Mustache template;

  Login(MustacheFactory mf, MemberService x1, SiteInfo x2) {
    this.memberService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("members/login.html");
  }

  void handleGetLogin(HttpExchange x1, FormData x2) {
    Map<String, Object> ys = x2.asMap();
    ys.put("pageTitle", "Login");
    addMenu(ys, siteInfo);
    WebUtil.renderForm(template, x1, ys);
  }

  void handlePostLogin(HttpExchange x1, FormData x2) {
    Result<String> result = Result
      .ok(x2)
      .map(WebUtil::form2cmd)
      .map(MemberCommand.class::cast)
      .map(memberService::handle)
      .map(MemberLoggedIn.class::cast)
      .map(MemberLoggedIn::member)
      .map(Member::id)
      .map(o -> createSession(o, memberService))
      .map(o -> addCookie(x1, siteInfo.cookieMaxAge().toSeconds(), o))
      .mapWrap(o -> redirect(x1, "/members/" + o.mid().val()));

    if (result.isErr()) {
      RuntimeException e = result.getError();
      if (e instanceof ValidationException e1) {
        x2.add("error", e1.getMessage());
        handleGetLogin(x1, x2);
      } else {
        throw e;
      }
    }
  }

  boolean isGetLogin(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/members/login")
    );
  }

  boolean isPostLogin(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches("/members/login")
    );
  }
}
