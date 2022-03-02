package com.acsredux.adapter.web.photodiary;

import static com.acsredux.adapter.web.members.Util.redirect;

import com.acsredux.adapter.web.auth.ACSHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.common.WebUtil;
import com.acsredux.adapter.web.views.CreatePhotoDiaryView;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.events.PhotoDiaryCreated;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.net.httpserver.HttpExchange;
import de.perschon.resultflow.Result;

public class CreateHandler {

  private final ContentService contentService;
  private final SiteInfo siteInfo;
  private final Mustache template;

  CreateHandler(MustacheFactory mf, ContentService x1, SiteInfo x2) {
    this.contentService = x1;
    this.siteInfo = x2;
    this.template = mf.compile("photo-diary/create.html");
  }

  void handleGetCreate(HttpExchange x1, FormData x2) {
    CreatePhotoDiaryView view = new CreatePhotoDiaryView(x1, x2, siteInfo);
    WebUtil.renderForm(template, x1, view);
  }

  public void handlePostCreate(HttpExchange x1, FormData x2) {
    Result<String> result = Result
      .ok(x2)
      .map(o -> WebUtil.form2cmd(ACSHttpPrincipal.of(x1.getPrincipal()), o))
      .map(BaseContentCommand.class::cast)
      .map(contentService::handle)
      .map(os -> os.get(0))
      .map(PhotoDiaryCreated.class::cast)
      .map(PhotoDiaryCreated::id)
      .map(o -> String.format("/photo-diary/%d", o.val()))
      .mapWrap(o -> redirect(x1, o));

    if (result.isErr()) {
      RuntimeException e = result.getError();
      if (e instanceof ValidationException e1) {
        x2.add("error", e1.getMessage());
        handleGetCreate(x1, x2);
      } else {
        throw e;
      }
    }
  }

  public boolean isGetCreate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("GET") &&
      x.getRequestURI().getPath().matches("/photo-diary/create")
    );
  }

  public boolean isPostCreate(HttpExchange x) {
    return (
      x.getRequestMethod().equalsIgnoreCase("POST") &&
      x.getRequestURI().getPath().matches("/photo-diary/create")
    );
  }
}
