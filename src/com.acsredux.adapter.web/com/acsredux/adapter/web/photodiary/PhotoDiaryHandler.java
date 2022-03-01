package com.acsredux.adapter.web.photodiary;

import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.articles.ArticleService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.util.List;

public class PhotoDiaryHandler extends BaseHandler {

  private final CreateHandler createHandler;

  public PhotoDiaryHandler(
    String templateRoot,
    ArticleService articleService,
    AdminService adminService
  ) {
    File f = new File(templateRoot);
    if (!f.exists()) {
      String fmt = "cant' open %s with current working directory %s";
      throw new IllegalStateException(
        String.format(fmt, f, System.getProperty("user.dir"))
      );
    }
    MustacheFactory mf = new DefaultMustacheFactory(f);
    SiteInfo siteInfo = adminService.getSiteInfo();
    this.createHandler = new CreateHandler(mf, articleService, siteInfo);
  }

  @Override
  protected List<Route> getRoutes() {
    return List.of(
      new Route(createHandler::isGetCreate, createHandler::handleGetCreate),
      new Route(createHandler::isPostCreate, createHandler::handlePostCreate)
    );
  }
}
