package com.acsredux.core.content.services;

import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.base.Command;
import com.acsredux.core.base.Event;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.PhotoAddedToDiary;
import com.acsredux.core.content.events.PhotoDiaryCreated;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.FileContent;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.content.values.Title;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.MemberPrincipal;
import java.time.InstantSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class ContentServiceProvider implements ContentService {

  private static final int STANDARD_HEIGHT_IN_PIXELS = 500;

  final ContentReader reader;
  final ContentWriter writer;
  final ImageWriter iwriter;
  final InstantSource clock;
  final ResourceBundle rb;

  public ContentServiceProvider(
    InstantSource c,
    ContentReader r,
    ContentWriter w,
    ImageWriter iw
  ) {
    this.clock = c;
    this.reader = r;
    this.writer = w;
    this.iwriter = iw;
    rb = ResourceBundle.getBundle("ContentErrorMessages");
  }

  @Override
  public Content getByID(ContentID x) {
    return reader.getByID(x);
  }

  @Override
  public List<Content> findArticlesByMemberID(MemberID x) {
    return reader.findByMemberID(x);
  }

  @Override
  public List<Event> handle(BaseContentCommand x) {
    return switch (x) {
      case CreatePhotoDiary x1 -> handleCreatePhotoDiary(x1);
      case UploadPhoto x1 -> handleUploadPhoto(x1);
    };
  }

  private List<Event> handleUploadPhoto(UploadPhoto x) {
    List<Event> ys = new ArrayList<>();

    MemberID mid = validateMemberLoggedIn(x);

    // Save original.
    FileName fn1 = x.fileName().insertSuffix("orig");
    ys.add(iwriter.save(clock.instant(), mid, x.content(), fn1));

    // Save our standard size.
    FileName fn2 = x.fileName().insertSuffix("std");
    FileContent resized = iwriter.resize(x.content(), STANDARD_HEIGHT_IN_PIXELS);
    ys.add(iwriter.save(clock.instant(), mid, resized, fn2));

    // Add photo to diary.
    writer.addPhotoToDiary(x, fn2);
    ys.add(new PhotoAddedToDiary(x));

    return ys;
  }

  private List<Event> handleCreatePhotoDiary(CreatePhotoDiary x) {
    MemberID mid = validateMemberLoggedIn(x);
    validateUniqueTitleForMember(mid, x.title());
    ContentID y = writer.createContent(x);
    return List.of(new PhotoDiaryCreated(x, y));
  }

  MemberID validateMemberLoggedIn(Command x) {
    Set<MemberPrincipal> ys = x.subject().getPrincipals(MemberPrincipal.class);
    if (ys.isEmpty()) {
      throw new AuthenticationException(rb.getString("not_logged_in1"));
    }
    return ys.stream().toList().get(0).mid();
  }

  void validateUniqueTitleForMember(MemberID x1, Title x2) {
    Optional<Content> doc = reader
      .findByMemberID(x1)
      .stream()
      .filter(o -> o.title().equals(x2))
      .findFirst();
    if (doc.isPresent()) {
      throw new ValidationException(rb.getString("title_exists"));
    }
  }
}
