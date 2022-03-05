package com.acsredux.adapter.web.photodiary;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.UploadPhotoView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;

public class UploadHandler {

  private final SiteInfo siteInfo;
  private final Mustache template;
  private final ContentService contentService;

  public UploadHandler(MustacheFactory mf, ContentService x1, SiteInfo x2) {
    this.contentService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("photo-diary/upload.html");
  }

  public void handleGetUpload(HttpExchange x1, FormData x2) {
    UploadPhotoView view = new UploadPhotoView(x1, x2, siteInfo);
    view.lookupContentInfo(contentService);
    WebUtil.renderForm(template, x1, view);
  }

  // /photo-diary/123/2022/add-image
  public boolean isGetUpload(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/photo-diary/\\d+/.*/add-image")
    );
  }
}
