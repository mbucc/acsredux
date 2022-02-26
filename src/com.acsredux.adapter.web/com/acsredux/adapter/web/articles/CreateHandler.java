package com.acsredux.adapter.web.articles;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.MemberService;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;

public class CreateHandler {

  public CreateHandler(
    MustacheFactory mf,
    MemberService memberService,
    SiteInfo siteInfo
  ) {}

  public boolean isGetCreateHandler(HttpExchange httpExchange) {
    return false;
  }

  public void handleGetCreateHandler(HttpExchange httpExchange, FormData formData) {}

  public boolean isPostCreateHandler(HttpExchange httpExchange) {
    return false;
  }

  public void handlePostCreateHandler(HttpExchange httpExchange, FormData formData) {}
}
