package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.members.Util;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.Image;
import com.acsredux.core.content.values.Paragraph;
import com.acsredux.core.content.values.Section;
import com.acsredux.core.content.values.SectionElement;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class UpdatePhotoDiaryView extends BaseView {

  final long contentID;
  String slug;
  List<SectionDTO> sections;

  interface El {
    String html();
  }

  record ImgEl(String url, String alt) implements El {
    @Override
    public String html() {
      StringBuilder sb = new StringBuilder();
      sb.append("<img url=\"");
      sb.append(url);
      sb.append("\"");
      if (!alt.isBlank()) {
        sb.append(" alt=\"");
        sb.append(alt);
        sb.append("\"");
      }
      sb.append(">");
      return sb.toString();
    }
  }

  record TxtEl(String txt) implements El {
    @Override
    public String html() {
      return txt;
    }
  }

  record SectionDTO(String title, List<El> elements) {
    public static SectionDTO of(Section s) {
      return new SectionDTO(
        s.title().val(),
        s.content().stream().map(SectionDTO::toEl).collect(Collectors.toList())
      );
    }

    private static El toEl(SectionElement x) {
      return switch (x) {
        case Image o -> new ImgEl(
          o.source().val().toString(),
          o.altText() == null ? "" : o.altText().val()
        );
        case Paragraph o -> new TxtEl(o.val());
      };
    }
  }

  public UpdatePhotoDiaryView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3);
    this.contentID = Util.uriToLong(x1.getRequestURI());
  }

  public void lookupContentInfo(ContentService x) {
    Content y = x.getByID(new ContentID(this.contentID));
    this.setPageTitle(y.title().val());
    this.slug = Util.titleToSlug(y.title().val());
    this.sections =
      y.sections().stream().map(SectionDTO::of).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", UpdatePhotoDiaryView.class.getSimpleName() + "[", "]")
      .add("pageTitle='" + pageTitle + "'")
      .add("menuItems=" + menuItems)
      .add("error='" + error + "'")
      .add("principalID=" + principalID)
      .add("principalName='" + principalName + "'")
      .add("isLoggedIn=" + isLoggedIn)
      .add("isAdmin=" + isAdmin)
      .add("isInAlphaTesting=" + isInAlphaTesting)
      .add("suggestionBoxURL='" + suggestionBoxURL + "'")
      .add("alphaTestMemberLimit=" + alphaTestMemberLimit)
      .add("contentID=" + contentID)
      .add("sections=" + sections)
      .toString();
  }
}
