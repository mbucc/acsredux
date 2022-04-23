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

public class MainHandler extends BaseHandler {

  private final DiaryHandler diaryHandler;
  private final PhotoHandler photoHandler;
  private final NoteHandler noteHandler;

  public MainHandler(
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
    this.diaryHandler = new DiaryHandler(mf, contentService, memberService, siteInfo);
    this.photoHandler = new PhotoHandler(mf, contentService, siteInfo);
    this.noteHandler = new NoteHandler(contentService);
  }

  @Override
  protected List<Route> getRoutes() {
    return List.of(
      new Route(diaryHandler::isEditDiary, diaryHandler::handleEditDiary),
      new Route(diaryHandler::isSaveDiary, diaryHandler::handleSaveDiary),
      new Route(diaryHandler::isViewDiary, diaryHandler::handleViewDiary),
      new Route(photoHandler::isEditPhoto, photoHandler::handleEditPhoto),
      new Route(photoHandler::isSavePhoto, photoHandler::handleSavePhoto),
      new Route(noteHandler::isSaveNote, noteHandler::handleSaveNote),
      new Route(noteHandler::isSaveNoteText, noteHandler::handleSaveNoteText)
    );
  }
}
