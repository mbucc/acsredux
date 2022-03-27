package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.sun.net.httpserver.HttpExchange;
import java.net.URI;

public class UploadPhotoView extends BaseView {

  final long contentID;
  String diaryName;

  public UploadPhotoView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3);
    this.contentID = uriToContentID(x1.getRequestURI());
  }

  static long uriToContentID(URI x) {
    return WebUtil.pathToID(x, 2);
  }

  public UploadPhotoView lookupContentInfo(ContentService x) {
    Content y = x.getByID(new ContentID(this.contentID));
    String title = String.format("Upload a photo to %s", y.title().val());
    this.diaryName = y.title().val();
    this.setPageTitle(title);
    return this;
  }
}
