package com.acsredux.adapter.web;

import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.BaseView;
import com.acsredux.adapter.web.views.PleaseEnableView;
import com.acsredux.adapter.web.views.RootView;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.util.List;

class RootHandler extends BaseHandler {

  private final MustacheFactory mf;
  private final AdminService adminService;

  RootHandler(String templateRoot, AdminService x1) {
    this.adminService = x1;
    this.mf = new DefaultMustacheFactory(new File(templateRoot));
  }

  @Override
  public List<Route> getRoutes() {
    return List.of(
      new Route(this::isRootPage, this::renderIndex),
      new Route(this::isPleaseEnable, this::renderPleaseEnable)
    );
  }

  private void renderIndex(HttpExchange x1, FormData x2) {
    SiteInfo siteInfo = adminService.getSiteInfo();
    BaseView view = new RootView(x1, x2, siteInfo);
    WebUtil.renderTemplate(mf, "index.html", view, x1);
  }

  private void renderPleaseEnable(HttpExchange x1, FormData x2) {
    SiteInfo siteInfo = adminService.getSiteInfo();
    BaseView view = new PleaseEnableView(x1, x2, siteInfo);
    WebUtil.renderTemplate(mf, "please_enable.html", view, x1);
  }

  boolean isRootPage(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      (
        x.getRequestURI().getPath().matches("/") ||
        x.getRequestURI().getPath().toLowerCase().matches("/index.html")
      )
    );
  }

  boolean isPleaseEnable(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().toLowerCase().matches("/please-enable-javascript.html")
    );
  }
}
