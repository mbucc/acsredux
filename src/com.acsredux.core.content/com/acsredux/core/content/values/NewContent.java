package com.acsredux.core.content.values;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.values.CreatedOn;
import java.util.Objects;

public record NewContent(
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
  public NewContent {
    die(createdBy, "null createdBy");
    if (contentType == ContentType.PHOTO_DIARY) {
      die(title, "null title");
    }
    die(createdOn, "null createdOn");
    die(contentType, "null contentType");
    die(blobType, "null blobType");
    from = from == null ? new FromDateTime(createdOn.val()) : from;
    body = body == null ? null : new BlobBytes(body.val().clone());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NewContent that = (NewContent) o;
    return (
      Objects.equals(refersTo, that.refersTo) &&
      createdBy.equals(that.createdBy) &&
      title.equals(that.title) &&
      createdOn.equals(that.createdOn) &&
      Objects.equals(from, that.from) &&
      Objects.equals(upto, that.upto) &&
      contentType == that.contentType &&
      blobType == that.blobType &&
      Objects.equals(body, that.body)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      refersTo,
      createdBy,
      title,
      createdOn,
      from,
      upto,
      contentType,
      blobType,
      body
    );
  }
}
