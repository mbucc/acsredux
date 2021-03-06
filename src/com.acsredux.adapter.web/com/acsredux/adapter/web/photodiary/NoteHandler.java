package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.common.WebUtil.created;
import static com.acsredux.adapter.web.common.WebUtil.handleError;
import static com.acsredux.adapter.web.common.WebUtil.pathToID;

import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormCommand;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.events.ContentCreated;
import com.acsredux.core.members.entities.Member;
import com.spencerwi.either.Result;
import com.sun.net.httpserver.HttpExchange;
import java.nio.charset.StandardCharsets;

public class NoteHandler {

  public static final String NOTE_URL = MainHandler.ROOT + "/\\d+/notes";
  private final ContentService contentService;

  public NoteHandler(ContentService x1) {
    this.contentService = x1;
  }

  public void handleSaveNote(HttpExchange x1, FormData x2) {
    Member m = ((MemberHttpPrincipal) x1.getPrincipal()).getMember();
    var y = Result
      .ok(x2)
      .map(o -> o.addCommand(FormCommand.SAVE_NOTE))
      .map(o -> o.normalizeDates(m.tz()))
      .map(o -> o.add("diaryID", "" + pathToID(x1, 2)))
      .map(o -> WebUtil.form2cmd(x1.getPrincipal(), o))
      .map(BaseContentCommand.class::cast)
      .map(contentService::handle);
    if (y.isOk()) {
      if (y.getResult().get(0) instanceof ContentCreated y1) {
        created(x1, String.valueOf(y1.id().val()).getBytes(StandardCharsets.UTF_8));
      } else {
        throw new IllegalStateException(
          "expected ContentCreated in " + y.getResult() + " for form data " + x2
        );
      }
    } else {
      handleError(x1, y);
    }
  }

  public boolean isSaveNote(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches(NOTE_URL)
    );
  }
}
