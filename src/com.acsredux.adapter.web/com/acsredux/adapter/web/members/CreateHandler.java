package com.acsredux.adapter.web.members;

import static com.acsredux.adapter.web.members.Util.addCookie;
import static com.acsredux.adapter.web.members.Util.createSession;
import static com.acsredux.adapter.web.members.Util.redirect;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.CreateMemberView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.BaseMemberCommand;
import com.acsredux.core.members.events.MemberAdded;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import de.perschon.resultflow.Result;

class CreateHandler {

  private final MemberService memberService;
  private final SiteInfo siteInfo;
  private final Mustache template;

  CreateHandler(MustacheFactory mf, MemberService x1, SiteInfo x2) {
    this.memberService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("members/create.html");
  }

  void handleGetCreate(HttpExchange x1, FormData x2) {
    CreateMemberView view = new CreateMemberView(x1, x2, siteInfo);
    view.lookupMemberCount(memberService);
    WebUtil.renderForm(template, x1, view);
  }

  void handlePostCreate(HttpExchange x1, FormData x2) {
    // Pipeline
    Result<String> result = Result
      .ok(x2)
      .map(o -> WebUtil.form2cmd(x1.getPrincipal(), o))
      .map(BaseMemberCommand.class::cast)
      .map(memberService::handle)
      .map(MemberAdded.class::cast)
      .map(MemberAdded::memberID)
      .map(o -> createSession(o, memberService))
      .map(o -> addCookie(x1, siteInfo.cookieMaxAge().toSeconds(), o))
      .mapWrap(o -> redirect(x1, MembersHandler.ROOT + "/" + o.mid().val()));

    if (result.isErr()) {
      RuntimeException e = result.getError();
      if (e instanceof ValidationException e1) {
        x2.add("error", e1.getMessage());
        handleGetCreate(x1, x2);
      } else {
        throw e;
      }
    }
  }

  boolean isGetCreate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches(MembersHandler.ROOT + "/create")
    );
  }

  boolean isPostCreate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches(MembersHandler.ROOT + "/create")
    );
  }
}
