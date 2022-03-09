package com.acsredux.core.content.entities;

import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.ImageSource;
import com.acsredux.core.content.values.PublishedDate;
import com.acsredux.core.content.values.Section;
import com.acsredux.core.content.values.SectionIndex;
import com.acsredux.core.content.values.Title;
import com.acsredux.core.members.values.MemberID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Content(
  ContentID id,
  MemberID author,
  Title title,
  List<Section> sections,
  PublishedDate published
) {
  public Content with(SectionIndex x1, ImageSource x2) {
    List<Section> ys = new ArrayList<>(sections.size());
    for (int i = 0; i < sections.size(); i++) {
      if (i == x1.val()) {
        ys.add(sections.get(x1.val()).with(x2));
      } else {
        ys.add(sections.get(i));
      }
    }
    return new Content(id, author, title, Collections.unmodifiableList(ys), published);
  }
}
