package com.acsredux.adapter.web.photodiary;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.UpdatePhotoDiaryView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.members.MemberService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;

public class ViewHandler {

  private final SiteInfo siteInfo;
  private final Mustache template;
  private final ContentService contentService;
  private final MemberService memberService;

  ViewHandler(
    MustacheFactory mf,
    ContentService contentService,
    SiteInfo x2,
    MemberService memberService
  ) {
    this.siteInfo = x2;
    this.contentService = contentService;
    this.template = mf.compile("photo-diary/view.html");
    this.memberService = memberService;
  }

  void handleGetUpdate(HttpExchange x1, FormData x2) {
    UpdatePhotoDiaryView view = new UpdatePhotoDiaryView(x1, x2, siteInfo);
    view.lookupContentInfo(contentService, memberService);
    WebUtil.renderForm(template, x1, view);
  }

  public boolean isGetUpdate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/photo-diary/\\d+$")
    );
  }
}
