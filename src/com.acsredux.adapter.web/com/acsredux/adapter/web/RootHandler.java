package com.acsredux.adapter.web;

import static com.acsredux.adapter.web.WebUtil.writeResponse;

import com.acsredux.adapter.web.auth.MemberPrincipal;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.MemberService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class RootHandler extends BaseHandler {

  private String templateRoot;
  private MustacheFactory mf;
  private AdminService adminService;

  RootHandler(MemberService x1, AdminService x2, String templateRoot) {
    super(x1);
    this.adminService = x2;
    this.templateRoot = templateRoot;
    this.mf = new DefaultMustacheFactory(new File(this.templateRoot));
  }

  @Override
  List<Route> getRoutes() {
    return Collections.singletonList(new Route(x -> true, this::renderIndex));
  }

  private void renderIndex(HttpExchange x1, FormData x2) {
    Mustache m = mf.compile("index.html");
    Writer writer = new StringWriter();
    Map<String, Object> view = x2.asMap();
    if (x1.getPrincipal() instanceof MemberPrincipal y1) {
      view.put("memberID", y1.getMember().id().val());
    }
    SiteInfo siteInfo = adminService.getSiteInfo();
    view.put("siteTitle", siteInfo.name());
    view.put("siteDescription", siteInfo.description());
    try {
      m.execute(writer, view).flush();
    } catch (Exception e) {
      throw new IllegalStateException(
        "error rendering template index.html with data " + Collections.emptyMap(),
        e
      );
    }
    byte[] body = writer.toString().getBytes();
    writeResponse(x1, body);
  }
}
