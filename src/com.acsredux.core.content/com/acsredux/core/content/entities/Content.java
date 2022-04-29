package com.acsredux.core.content.entities;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.values.CreatedOn;
import java.util.Comparator;

public record Content(
  ContentID id,
  ContentID refersTo,
  MemberID createdBy,
  Title title,

  CreatedOn createdOn,
  FromDateTime from,
  UptoDateTime upto,

  ContentType contentType,
  BlobType blobType,
  BlobBytes body
) {
  public static Comparator<Content> byFrom = Comparator.comparingLong(o ->
    o.from().val().getEpochSecond()
  );

  public Content withBody(BlobBytes body) {
    return new Content(
      this.id,
      this.refersTo,
      this.createdBy,
      this.title,
      this.createdOn,
      this.from,
      this.upto,
      this.contentType,
      this.blobType,
      body
    );
  }
}
