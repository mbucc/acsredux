package com.acsredux.adapter.web;

import static com.acsredux.adapter.web.common.WebUtil.writeResponse;

import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.views.BaseView;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

class RootHandler extends BaseHandler {

  private final MustacheFactory mf;
  private final AdminService adminService;

  RootHandler(String templateRoot, AdminService x1) {
    this.adminService = x1;
    this.mf = new DefaultMustacheFactory(new File(templateRoot));
  }

  @Override
  public List<Route> getRoutes() {
    return Collections.singletonList(new Route(x -> true, this::renderIndex));
  }

  class RootView extends BaseView {

    public final String siteDescription;

    public RootView(HttpExchange x1, FormData x2, SiteInfo x3) {
      super(x1, x2, x3, x3.name());
      this.siteDescription = x3.description();
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", RootView.class.getSimpleName() + "[", "]")
        .add("siteDescription='" + siteDescription + "'")
        .add("pageTitle='" + pageTitle + "'")
        .add("menuItems=" + menuItems)
        .add("error='" + error + "'")
        .add("principalID=" + principalID)
        .add("principalName='" + principalName + "'")
        .add("isLoggedIn=" + isLoggedIn)
        .add("isAdmin=" + isAdmin)
        .add("isInAlphaTesting=" + isInAlphaTesting)
        .add("suggestionBoxURL='" + suggestionBoxURL + "'")
        .add("alphaTestMemberLimit=" + alphaTestMemberLimit)
        .toString();
    }
  }

  private void renderIndex(HttpExchange x1, FormData x2) {
    Mustache m = mf.compile("index.html");
    Writer writer = new StringWriter();
    SiteInfo siteInfo = adminService.getSiteInfo();
    RootView view = new RootView(x1, x2, siteInfo);
    try {
      m.execute(writer, view).flush();
    } catch (Exception e) {
      throw new IllegalStateException(
        "error rendering template index.html with data " + view,
        e
      );
    }
    byte[] body = writer.toString().getBytes();
    writeResponse(x1, body);
  }
}
