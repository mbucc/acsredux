package com.acsredux.adapter.filesystem;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.PublishedDate;
import com.acsredux.core.content.values.Title;
import com.acsredux.core.members.values.MemberID;
import java.time.Instant;
import java.util.List;

public class ContentDTO {

  long id;
  long author;
  String title;
  List<SectionDTO> sections;
  long published;

  ContentDTO() {}

  ContentDTO(Content x) {
    this.id = x.id().val();
    this.author = x.author().val();
    this.title = x.title().val();
    this.sections = x.sections().stream().map(SectionDTO::new).toList();
    this.published = x.published().val().getEpochSecond();
  }

  public Content asContent() {
    return new Content(
      new ContentID(id),
      new MemberID(author),
      new Title(title),
      sections.stream().map(SectionDTO::asSection).toList(),
      new PublishedDate(Instant.ofEpochSecond(published))
    );
  }
}
