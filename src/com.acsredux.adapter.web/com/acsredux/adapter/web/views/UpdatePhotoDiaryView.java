package com.acsredux.adapter.web.views;

import static java.time.format.TextStyle.SHORT_STANDALONE;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.members.Util;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.ContentType;
import com.acsredux.core.members.MemberService;
import com.sun.net.httpserver.HttpExchange;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UpdatePhotoDiaryView extends BaseView {

  final long contentID;
  long memberID;
  String slug;
  List<SectionDTO> sections;

  record SectionDTO(String title, List<Map<String, Object>> elements) {}

  public UpdatePhotoDiaryView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3);
    this.contentID = Util.uriToLong(x1.getRequestURI());
  }

  private static Map<String, Object> asMap(Content x, ZoneId tz) {
    // TODO: DRY up all the URL paths.
    String srcfmt = "/static/members/%d/%s";
    return switch (x.contentType()) {
      case PHOTO -> Map.of(
        "src",
        String.format(srcfmt, x.createdBy().val(), x.content().asString()),
        "alt",
        x.from().asString(tz),
        "contentID",
        x.id().val()
      );
      case DIARY_ENTRY -> Map.of("text", x.content().asString());
      default -> throw new IllegalStateException("unexpected type: " + x.contentType());
    };
  }

  public void lookupContentInfo(ContentService x1, MemberService x2) {
    ContentID cid = new ContentID(this.contentID);
    Content y = x1.getByID(cid);
    this.memberID = y.createdBy().val();
    ZoneId tz = x2.getByID(y.createdBy()).tz();
    this.setPageTitle(y.title().val());
    this.slug = Util.titleToSlug(y.title().val());

    this.sections = new ArrayList<>();
    List<Content> children = x1.findChildrenOfID(cid);
    for (Month m : Month.values()) {
      this.sections.add(
          new SectionDTO(
            m.getDisplayName(SHORT_STANDALONE, Locale.getDefault()),
            children
              .stream()
              .filter(o -> o.from().getMonthValue(tz) == m)
              .filter(o ->
                o.contentType().equals(ContentType.PHOTO) ||
                o.contentType().equals(ContentType.DIARY_ENTRY)
              )
              .sorted(Content.byFrom)
              .map(o -> asMap(o, tz))
              .toList()
          )
        );
    }
  }

  public boolean isMyPage() {
    return Objects.equals(memberID, principalID);
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
}
