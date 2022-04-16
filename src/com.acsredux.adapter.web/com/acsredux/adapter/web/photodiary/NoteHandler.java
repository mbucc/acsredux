package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.common.WebUtil.internalError;
import static com.acsredux.adapter.web.common.WebUtil.pathToID;
import static com.acsredux.adapter.web.members.Util.redirect;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.base.NotAuthorizedException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.members.entities.Member;
import com.spencerwi.either.Result;
import com.sun.net.httpserver.HttpExchange;

public class NoteHandler {

  public static final String NOTE_URL = "/photo-diary/\\d+/add-note";
  private final ContentService contentService;

  public NoteHandler(ContentService x1) {
    this.contentService = x1;
  }

  public void handleSaveNote(HttpExchange x1, FormData x2) {
    Member m = ((MemberHttpPrincipal) x1.getPrincipal()).getMember();
    var y = Result
      .ok(x2)
      .map(o -> o.add("command", "ADD_NOTE"))
      .map(o -> o.add("parent", "" + pathToID(x1, 2)))
      .map(o -> WebUtil.form2cmd(x1.getPrincipal(), o))
      .map(BaseContentCommand.class::cast)
      .map(contentService::handle);
    if (y.isOk()) {
      var contentID = x2.get("parent");
      var location = String.format("/photo-diary/%s", contentID);
      redirect(x1, location);
    } else {
      Exception e = y.getException();
      if (e instanceof ValidationException e1) {
        x2.add("error", e1.getMessage());
      } else if (e instanceof NotAuthorizedException e1) {
        x2.add("error", e1.getMessage());
      } else {
        internalError(x1, e);
      }
    }
  }

  public boolean isSaveNote(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches(NOTE_URL)
    );
  }
}
