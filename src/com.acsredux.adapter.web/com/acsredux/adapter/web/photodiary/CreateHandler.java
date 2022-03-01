package com.acsredux.adapter.web.photodiary;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.CreateArticleView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.articles.ArticleService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;

public class CreateHandler {

  private final ArticleService articleService;
  private final SiteInfo siteInfo;
  private final Mustache template;

  CreateHandler(MustacheFactory mf, ArticleService x1, SiteInfo x2) {
    this.articleService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("photo-diary/create.html");
  }

  void handleGetCreate(HttpExchange x1, FormData x2) {
    CreateArticleView view = new CreateArticleView(x1, x2, siteInfo);
    WebUtil.renderForm(template, x1, view);
  }

  public void handlePostCreate(HttpExchange x1, FormData x2) {
    // Pipeline
    //    Result<String> result = Result
    //      .ok(x2)
    //      .map(o -> WebUtil.form2cmd(x1.getPrincipal(), o))
    //      .map(BaseMemberCommand.class::cast)
    //      .map(articleService::handle)
    //      .map(MemberAdded.class::cast)
    //      .map(MemberAdded::memberID)
    //      .map(o -> createSession(o, memberService))
    //      .map(o -> addCookie(x1, siteInfo.cookieMaxAge().toSeconds(), o))
    //      .mapWrap(o -> redirect(x1, "/members/" + o.mid().val()));
    //
    //    if (result.isErr()) {
    //      RuntimeException e = result.getError();
    //      if (e instanceof ValidationException e1) {
    //        x2.add("error", e1.getMessage());
    //        handleGetCreate(x1, x2);
    //      } else {
    //        throw e;
    //      }
    //    }
  }

  public boolean isGetCreate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/photo-diary/create")
    );
  }

  public boolean isPostCreate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches("/photo-diary/create")
    );
  }
}
