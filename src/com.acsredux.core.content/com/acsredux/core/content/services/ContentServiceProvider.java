package com.acsredux.core.content.services;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.PhotoDiaryCreated;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.MemberID;
import java.time.InstantSource;
import java.util.List;

public class ContentServiceProvider implements ContentService {

  final ContentReader reader;
  final ContentWriter writer;

  public ContentServiceProvider(ContentReader r, ContentWriter w, InstantSource c) {
    this.reader = r;
    this.writer = w;
  }

  @Override
  public Content getByID(ContentID x) {
    return reader.getContent(x);
  }

  @Override
  public List<Content> findArticlesByMemberID(MemberID x) {
    return reader.findContentByMemberID(x);
  }

  @Override
  public List<Event> handle(BaseContentCommand x) {
    if (x instanceof CreatePhotoDiary x1) {
      return handle(x1);
    } else {
      throw new IllegalStateException("invalid command " + x);
    }
  }

  private List<Event> handle(CreatePhotoDiary x) {
    ContentID y = writer.createContent(x);
    return List.of(new PhotoDiaryCreated(x, y));
  }
}
