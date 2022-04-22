package com.acsredux.adapter.web.views;

import static com.acsredux.adapter.web.common.WebUtil.renderMarkdown;
import static java.time.format.TextStyle.SHORT_STANDALONE;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.members.Util;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.ContentType;
import com.sun.net.httpserver.HttpExchange;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiaryView extends BaseView {

  public static final String NOTE = "note";
  public static final String PHOTO_ENTRY = "photo";
  public static final String ENTRY_TYPE = "type";
  final long contentID;
  long memberID;
  String slug;
  List<SectionDTO> sections;

  /**
   * A Section DTO represents a single month and contains a list of diary
   * entries for that month.  The section title is the month name.
   *
   * Each diary entry is represented as a map so we can pass different object
   * types to the mustache template.
   */
  record SectionDTO(String title, List<Map<String, Object>> entries) {}

  public DiaryView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3);
    this.contentID = Util.uriToLong(x1.getRequestURI());
  }

  public void addDiaryInfo(Content y) {
    this.memberID = y.createdBy().val();
    this.setPageTitle(y.title().val());
    this.slug = Util.titleToSlug(y.title().val());
  }

  public boolean isMyPage() {
    return Objects.equals(memberID, principalID);
  }

  public ContentID contentID() {
    return new ContentID(this.contentID);
  }

  public void setSections(List<SectionDTO> xs) {
    this.sections = new ArrayList<>(xs);
  }

  @Override
  public String toString() {
    return (
      "UpdatePhotoDiaryView{" +
      "menuItems=" +
      menuItems +
      ", pageTitle='" +
      pageTitle +
      '\'' +
      ", error='" +
      error +
      '\'' +
      ", principalID=" +
      principalID +
      ", principalName='" +
      principalName +
      '\'' +
      ", isLoggedIn=" +
      isLoggedIn +
      ", isAdmin=" +
      isAdmin +
      ", isInAlphaTesting=" +
      isInAlphaTesting +
      ", suggestionBoxURL='" +
      suggestionBoxURL +
      '\'' +
      ", alphaTestMemberLimit=" +
      alphaTestMemberLimit +
      ", analyticsScriptTag='" +
      analyticsScriptTag +
      '\'' +
      ", contentID=" +
      contentID +
      ", memberID=" +
      memberID +
      ", slug='" +
      slug +
      '\'' +
      ", sections=" +
      sections +
      '}'
    );
  }

  private static Map<String, Object> asMap(Content x, ZoneId tz) {
    return switch (x.contentType()) {
      case PHOTO -> asPhotoMap(x, tz);
      case DIARY_ENTRY -> asNoteMap(x.content());
      default -> throw new IllegalStateException("unexpected type: " + x.contentType());
    };
  }

  private static Map<String, Object> asPhotoMap(Content x, ZoneId tz) {
    return Map.of(
      ENTRY_TYPE,
      PHOTO_ENTRY,
      "src",
      // TODO: DRY up the URL paths.
      String.format("/static/members/%d/%s", x.createdBy().val(), x.content().asString()),
      "alt",
      x.from().asString(tz),
      "photoContentID",
      x.id().val()
    );
  }

  private static Map<String, Object> asNoteMap(BlobBytes x) {
    return Map.of(
      ENTRY_TYPE,
      NOTE,
      "text",
      x == null ? "" : renderMarkdown(x.asString())
    );
  }

  public static List<SectionDTO> buildSections(
    List<Content> children,
    ZoneId authorTimeZone
  ) {
    var ys = new ArrayList<SectionDTO>();
    for (Month month : Month.values()) {
      ys.add(
        new SectionDTO(
          getMonthName(month),
          children
            .stream()
            .filter(o -> o.from().getMonthValue(authorTimeZone) == month)
            .filter(o ->
              o.contentType().equals(ContentType.PHOTO) ||
              o.contentType().equals(ContentType.DIARY_ENTRY)
            )
            .sorted(Content.byFrom)
            .map(o -> asMap(o, authorTimeZone))
            .collect(Collectors.toList())
        )
      );
    }

    // Make sure each month starts with a summary note.
    for (SectionDTO y : ys) {
      if (y.entries.isEmpty() || !y.entries.get(0).get(ENTRY_TYPE).equals(NOTE)) {
        y.entries.add(0, asNoteMap(null));
      }
    }
    return ys;
  }

  static String getMonthName(Month month) {
    return month.getDisplayName(SHORT_STANDALONE, Locale.getDefault());
  }
}
