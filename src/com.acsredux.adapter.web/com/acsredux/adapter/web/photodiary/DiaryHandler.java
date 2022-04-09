package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.members.Util.redirect;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.DiaryView;
import com.acsredux.adapter.web.views.EditDiaryView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.events.ContentCreated;
import com.acsredux.core.members.MemberService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import de.perschon.resultflow.Result;

public class DiaryHandler {

  private final ContentService contentService;
  private final MemberService memberService;
  private final SiteInfo siteInfo;
  private final Mustache editTemplate;
  private final Mustache viewTemplate;
  private final Mustache viewTemplateMine;

  DiaryHandler(MustacheFactory mf, ContentService x1, MemberService x2, SiteInfo x3) {
    this.contentService = x1;
    this.memberService = x2;
    this.siteInfo = x3;
    this.editTemplate = mf.compile("photo-diary/create.html");
    this.viewTemplate = mf.compile("photo-diary/view.html");
    this.viewTemplateMine = mf.compile("photo-diary/view_by_editor.html");
  }

  void handleViewDiary(HttpExchange x1, FormData x2) {
    DiaryView view = new DiaryView(x1, x2, siteInfo);
    view.lookupContentInfo(contentService, memberService);
    Mustache t = viewTemplate;
    if (view.isMyPage()) {
      t = viewTemplateMine;
    }
    WebUtil.renderForm(t, x1, view);
  }

  void handleEditDiary(HttpExchange x1, FormData x2) {
    EditDiaryView view = new EditDiaryView(x1, x2, siteInfo);
    WebUtil.renderForm(editTemplate, x1, view);
  }

  public void handleSaveDiary(HttpExchange x1, FormData x2) {
    Result<String> result = Result
      .ok(x2)
      .map(o -> WebUtil.form2cmd(x1.getPrincipal(), o))
      .map(BaseContentCommand.class::cast)
      .map(contentService::handle)
      .map(os -> os.get(0))
      .map(ContentCreated.class::cast)
      .map(ContentCreated::id)
      .map(o -> String.format("/photo-diary/%d", o.val()))
      .mapWrap(o -> redirect(x1, o));

    if (result.isErr()) {
      RuntimeException e = result.getError();
      if (e instanceof ValidationException e1) {
        x2.add("error", e1.getMessage());
        handleEditDiary(x1, x2);
      } else {
        throw e;
      }
    }
  }

  public boolean isEditDiary(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/photo-diary/create")
    );
  }

  public boolean isSaveDiary(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches("/photo-diary/create")
    );
  }

  public boolean isViewDiary(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/photo-diary/\\d+$")
    );
  }
}
