package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.common.WebUtil.internalError;
import static com.acsredux.adapter.web.common.WebUtil.pathToID;
import static com.acsredux.adapter.web.members.Util.redirect;
import static com.acsredux.adapter.web.photodiary.PhotoHandler.normalizeDates;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.EditNoteView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.NotAuthorizedException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.members.entities.Member;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.spencerwi.either.Result;
import com.sun.net.httpserver.HttpExchange;

public class NoteHandler {

  public static final String NOTE_URL = "/photo-diary/\\d+/add-note";
  private final SiteInfo siteInfo;
  private final Mustache template;
  private final ContentService contentService;

  public NoteHandler(MustacheFactory mf, ContentService x1, SiteInfo x2) {
    this.contentService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("photo-diary/edit_note.html");
  }

  public void handleEditNote(HttpExchange x1, FormData x2) {
    var y = Result
      .ok(new EditNoteView(x1, x2, siteInfo))
      .map(o -> o.lookupContentInfo(contentService))
      .map(o -> WebUtil.renderForm(template, x1, o));
    if (y.isErr()) {
      internalError(x1, y.getException());
    }
  }

  public void handleSaveNote(HttpExchange x1, FormData x2) {
    Member m = ((MemberHttpPrincipal) x1.getPrincipal()).getMember();
    var y = Result
      .ok(x2)
      .map(o -> o.add("command", "UPLOAD_PHOTO"))
      .map(o -> normalizeDates(o, m.tz()))
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
        handleEditNote(x1, x2);
      } else if (e instanceof NotAuthorizedException e1) {
        x2.add("error", e1.getMessage());
        handleEditNote(x1, x2);
      } else {
        internalError(x1, e);
      }
    }
  }

  // /photo-diary/123/add-note
  public boolean isEditNote(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches(NOTE_URL)
    );
  }

  public static boolean isSaveNote(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches(NOTE_URL)
    );
  }
}