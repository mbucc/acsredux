package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.members.Util;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.Image;
import com.acsredux.core.content.values.Paragraph;
import com.acsredux.core.content.values.SectionElement;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdatePhotoDiaryView extends BaseView {

  final long contentID;
  long memberID;
  String slug;
  List<SectionDTO> sections;

  interface El {
    String html();
  }

  record ImgEl(String url, String alt) implements El {
    @Override
    public String html() {
      StringBuilder sb = new StringBuilder();
      sb.append("<img src=\"");
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

  record SectionDTO(String title, int sectionIndex, List<El> elements) {
    private static El toEl(SectionElement x) {
      return switch (x) {
        case Image o -> new ImgEl(
          o.source().val(),
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
    this.memberID = y.author().val();
    this.setPageTitle(y.title().val());
    this.slug = Util.titleToSlug(y.title().val());
    this.sections = new ArrayList<>();
    for (int i = 0; i < y.sections().size(); i++) {
      this.sections.add(
          new SectionDTO(
            y.sections().get(i).title().val(),
            i,
            y
              .sections()
              .get(i)
              .content()
              .stream()
              .map(SectionDTO::toEl)
              .collect(Collectors.toList())
          )
        );
    }
  }

  public boolean isMyPage() {
    return Objects.equals(memberID, principalID);
  }
}
