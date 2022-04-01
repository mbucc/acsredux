package com.acsredux.adapter.web.content;

import static com.acsredux.adapter.web.common.WebUtil.asSubject;
import static com.acsredux.adapter.web.common.WebUtil.internalError;
import static com.acsredux.adapter.web.common.WebUtil.notAuthorized;

import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.base.NotAuthorizedException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.DeleteContent;
import com.acsredux.core.content.values.ContentID;
import com.spencerwi.either.Result;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;

public class ContentHandler extends BaseHandler {

  public static final String IMAGE_URL = "/images/\\d+";

  private final ContentService contentService;

  public ContentHandler(ContentService contentService) {
    this.contentService = contentService;
  }

  @Override
  protected List<Route> getRoutes() {
    return List.of(new Route(this::isDeleteContent, this::handleDeleteContent));
  }

  private void handleDeleteContent(HttpExchange x1, FormData x2) {
    var y = Result
      .ok(x1)
      .map(o -> WebUtil.pathToID(x1, 2)) // path = /images/2
      .map(ContentID::new)
      .map(o -> new DeleteContent(asSubject(x1.getPrincipal()), o))
      .map(this.contentService::handle);
    if (y.isOk()) {
      WebUtil.ok(x1);
    } else {
      Exception e = y.getException();
      switch (e) {
        case ValidationException e1 -> internalError(x1, e1);
        case NotAuthorizedException e1 -> notAuthorized(x1, e1);
        default -> internalError(x1, e);
      }
    }
  }

  private boolean isDeleteContent(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("DELETE") &&
      x.getRequestURI().getPath().matches(IMAGE_URL)
    );
  }
}
