package com.acsredux.adapter.filesystem;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.values.CreatedOn;
import java.time.Instant;

public class ContentDTO {

  long id;
  Long refersTo;
  long createdBy;
  String title;
  long created;
  long from;
  Long upto;
  String contentType;
  String blobType;
  String content;

  ContentDTO() {}

  ContentDTO(Content x) {
    try {
      this.id = x.id().val();
      this.refersTo = x.refersTo() == null ? null : x.refersTo().val();
      this.createdBy = x.createdBy().val();
      this.title = x.title().val();
      this.created = x.createdOn().val().getEpochSecond();
      this.from = x.from().val().getEpochSecond();
      this.upto = x.upto() == null ? null : x.upto().val().getEpochSecond();
      this.contentType = x.contentType().name();
      this.blobType = x.blobType().name();
      this.content = x.content() == null ? null : x.content().asString();
    } catch (Exception e) {
      throw new IllegalStateException("error converting to DTO: " + x);
    }
  }

  public Content asContent() {
    return new Content(
      new ContentID(id),
      this.refersTo == null ? null : new ContentID(this.refersTo),
      new MemberID(createdBy),
      new Title(title),
      new CreatedOn(Instant.ofEpochSecond(created)),
      new FromDateTime(Instant.ofEpochSecond(from)),
      upto == null ? null : new UptoDateTime(Instant.ofEpochSecond(from)),
      ContentType.valueOf(contentType),
      BlobType.valueOf(blobType),
      content == null ? null : BlobBytes.ofString(content)
    );
  }
}
