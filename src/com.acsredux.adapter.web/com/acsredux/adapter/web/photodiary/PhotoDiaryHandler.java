package com.acsredux.adapter.web.photodiary;

import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.members.MemberService;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.util.List;

public class PhotoDiaryHandler extends BaseHandler {

  private final CreateHandler createHandler;
  private final ViewHandler updateHandler;
  private final UploadPhotoHandler uploadHandler;

  public PhotoDiaryHandler(
    String templateRoot,
    ContentService contentService,
    AdminService adminService,
    MemberService memberService
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
    this.createHandler = new CreateHandler(mf, contentService, siteInfo);
    this.updateHandler = new ViewHandler(mf, contentService, siteInfo, memberService);
    this.uploadHandler = new UploadPhotoHandler(mf, contentService, siteInfo);
  }

  @Override
  protected List<Route> getRoutes() {
    return List.of(
      new Route(createHandler::isGetCreate, createHandler::handleGetCreate),
      new Route(createHandler::isPostCreate, createHandler::handlePostCreate),
      new Route(updateHandler::isGetUpdate, updateHandler::handleGetUpdate),
      new Route(uploadHandler::isGetUpload, uploadHandler::handleGetUpload),
      new Route(UploadPhotoHandler::isPostUpload, uploadHandler::handlePostUpload)
    );
  }
}
