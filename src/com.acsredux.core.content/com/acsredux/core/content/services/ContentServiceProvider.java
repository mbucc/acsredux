package com.acsredux.core.content.services;

import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.base.Event;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.PhotoDiaryCreated;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.Title;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.MemberPrincipal;
import java.time.InstantSource;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class ContentServiceProvider implements ContentService {

  final ContentReader reader;
  final ContentWriter writer;
  final ResourceBundle rb;

  public ContentServiceProvider(ContentReader r, ContentWriter w, InstantSource c) {
    this.reader = r;
    this.writer = w;
    rb = ResourceBundle.getBundle("ContentErrorMessages");
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
    MemberID mid = validateMemberLoggedIn(x);
    validateUniqueTitleForMember(mid, x.title());
    ContentID y = writer.createContent(x);
    return List.of(new PhotoDiaryCreated(x, y));
  }

  MemberID validateMemberLoggedIn(CreatePhotoDiary x) {
    Set<MemberPrincipal> ys = x.subject().getPrincipals(MemberPrincipal.class);
    if (ys.isEmpty()) {
      throw new AuthenticationException(rb.getString("not_logged_in1"));
    }
    return ys.stream().toList().get(0).mid();
  }

  void validateUniqueTitleForMember(MemberID x1, Title x2) {
    Optional<Content> doc = reader
      .findContentByMemberID(x1)
      .stream()
      .filter(o -> o.title().equals(x2))
      .findFirst();
    if (doc.isPresent()) {
      throw new ValidationException(rb.getString("title_exists"));
    }
  }
}
