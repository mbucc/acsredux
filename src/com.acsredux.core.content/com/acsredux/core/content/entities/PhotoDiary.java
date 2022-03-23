package com.acsredux.core.content.entities;

import static com.acsredux.core.base.Util.die;

import com.acsredux.core.content.values.BlobType;
import java.util.ArrayList;
import java.util.List;

/**
 * A top-level unit of content; for example, a photo diary.
 *
 * An article must be a top-level content item (that is,
 * refersTo must be null).
 *
 * See https://github.com/commonmark/commonmark-java.
 */
public record PhotoDiary(Content content, List<Content> entries, List<Content> comments) {
  public PhotoDiary {
    die(content, "null content");
    if (content.blobType() != BlobType.MARKDOWN) {
      throw new IllegalStateException("a article must be Markdown");
    }
    if (content.refersTo() != null) {
      throw new IllegalStateException("an article must be a top-level page");
    }
    if (comments == null) {
      comments = new ArrayList<>();
    }
  }
}
