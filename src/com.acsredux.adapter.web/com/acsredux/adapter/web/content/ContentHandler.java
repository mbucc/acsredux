package com.acsredux.adapter.web.content;

import static com.acsredux.adapter.web.common.WebUtil.asSubject;
import static com.acsredux.adapter.web.common.WebUtil.handleError;
import static com.acsredux.adapter.web.common.WebUtil.internalError;
import static com.acsredux.adapter.web.common.WebUtil.notAuthorized;
import static com.acsredux.adapter.web.common.WebUtil.ok;
import static com.acsredux.adapter.web.common.WebUtil.pathToID;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.BaseHandler;
import com.acsredux.adapter.web.common.FormCommand;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.base.NotAuthorizedException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.commands.DeleteContent;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.entities.Member;
import com.spencerwi.either.Result;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;

public class ContentHandler extends BaseHandler {

  public static final String ROOT = "/content";
  public static final String IMAGE_URL = ROOT + "/\\d+";
  public static final String CONTENT_BODY_URL = ROOT + "/\\d+/body";

  private final ContentService contentService;

  public ContentHandler(ContentService contentService) {
    this.contentService = contentService;
  }

  @Override
  protected List<Route> getRoutes() {
    return List.of(
      new Route(this::isDelete, this::handleDeleteContent),
      new Route(this::isSaveBody, this::handleSaveBody)
    );
  }

  private void handleDeleteContent(HttpExchange x1, FormData x2) {
    var y = Result
      .ok(x1)
      .map(o -> WebUtil.pathToID(x1, 2)) // path = /content/2
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

  public void handleSaveBody(HttpExchange x1, FormData x2) {
    Member m = ((MemberHttpPrincipal) x1.getPrincipal()).getMember();
    var y = Result
      .ok(x2)
      .map(o -> o.addCommand(FormCommand.SAVE_NOTE_TEXT))
      .map(o -> o.add("noteID", "" + pathToID(x1, 2)))
      .map(o -> WebUtil.form2cmd(x1.getPrincipal(), o))
      .map(BaseContentCommand.class::cast)
      .map(contentService::handle);
    if (y.isOk()) {
      ok(x1);
    } else {
      handleError(x1, y);
    }
  }

  private boolean isDelete(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("DELETE") &&
      x.getRequestURI().getPath().matches(IMAGE_URL)
    );
  }

  public boolean isSaveBody(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("PUT") &&
      x.getRequestURI().getPath().matches(CONTENT_BODY_URL)
    );
  }
}
