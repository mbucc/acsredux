package com.acsredux.adapter.filesystem;

import static com.acsredux.adapter.filesystem.SectionDTO.ElementType.IMAGE;
import static com.acsredux.adapter.filesystem.SectionDTO.ElementType.TEXT;

import com.acsredux.core.content.values.AltText;
import com.acsredux.core.content.values.Image;
import com.acsredux.core.content.values.ImageSource;
import com.acsredux.core.content.values.Paragraph;
import com.acsredux.core.content.values.Section;
import com.acsredux.core.content.values.SectionElement;
import com.acsredux.core.content.values.Title;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SectionDTO {

  String title;
  List<Map<String, String>> content;

  SectionDTO() {}

  enum ElementType {
    IMAGE,
    TEXT,
  }

  SectionDTO(Section x) {
    this.title = x.title().val();
    this.content = new ArrayList<>(x.content().size());
    for (SectionElement el : x.content()) {
      this.content.add(
          switch (el) {
            case Image x1 -> Map.of(
              "elementType",
              IMAGE.name(),
              "src",
              x1.source().val(),
              "alt",
              x1.altText().val()
            );
            case Paragraph x1 -> Map.of(
              "elementType",
              TEXT.name(),
              "paragraph",
              x1.val()
            );
          }
        );
    }
  }

  public Section asSection() {
    List<SectionElement> ys = new ArrayList<>(content.size());
    for (Map<String, String> x : content) {
      ElementType elementType = ElementType.valueOf(x.get("elementType"));
      SectionElement y =
        switch (elementType) {
          case IMAGE -> new Image(
            new ImageSource(x.get("src")),
            x.get("src") == null ? null : new AltText(x.get("alt"))
          );
          case TEXT -> new Paragraph(x.get("paragraph"));
        };
      ys.add(y);
    }
    return new Section(new Title(title), ys);
  }
}
