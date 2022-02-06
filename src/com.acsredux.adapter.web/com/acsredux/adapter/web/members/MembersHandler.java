package com.acsredux.adapter.web.members;

import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.MemberService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.util.List;

public class MembersHandler extends BaseHandler {

  private final Login loginHandler;
  private final Create createHandler;
  private final Logout logoutHandler;
  final Dashboard dashboardHandler;

  public MembersHandler(
    String templateRoot,
    MemberService memberService,
    AdminService adminService
  ) {
    super(memberService);
    File f = new File(templateRoot);
    if (!f.exists()) {
      String fmt = "cant' open %s with current working directory %s";
      throw new IllegalStateException(
        String.format(fmt, f, System.getProperty("user.dir"))
      );
    }
    MustacheFactory mf = new DefaultMustacheFactory(f);
    SiteInfo siteInfo = adminService.getSiteInfo();
    this.loginHandler = new Login(mf, memberService, siteInfo);
    this.createHandler = new Create(mf, memberService, siteInfo);
    this.logoutHandler = new Logout();
    this.dashboardHandler = new Dashboard(mf, memberService, siteInfo);
  }

  @Override
  protected List<Route> getRoutes() {
    return List.of(
      new Route(logoutHandler::isGetLogout, logoutHandler::handleLogout),
      new Route(loginHandler::isGetLogin, loginHandler::handleGetLogin),
      new Route(loginHandler::isPostLogin, loginHandler::handlePostLogin),
      new Route(dashboardHandler::isGetDashboard, dashboardHandler::handleGetDashboard),
      new Route(createHandler::isGetCreate, createHandler::handleGetCreate),
      new Route(createHandler::isPostCreate, createHandler::handlePostCreate)
    );
  }
}
