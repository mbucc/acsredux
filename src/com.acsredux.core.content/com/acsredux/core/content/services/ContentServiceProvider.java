package com.acsredux.core.content.services;

import com.acsredux.core.base.*;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.DeleteContent;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.ContentCreated;
import com.acsredux.core.content.events.ContentDeleted;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageReader;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.values.CreatedOn;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.InstantSource;
import java.util.*;

public class ContentServiceProvider implements ContentService {

  public static final String STD_IMAGE = "std";
  public static final String ORIGINAL_IMAGE = "orig";
  final ContentReader reader;
  final ContentWriter writer;
  final ImageWriter iwriter;
  final InstantSource clock;
  final ResourceBundle rb;

  BlobBytes placeholderImageBytes;

  public ContentServiceProvider(
    InstantSource c,
    ContentReader r,
    ContentWriter w,
    ImageReader ir,
    ImageWriter iw
  ) {
    this.clock = c;
    this.reader = r;
    this.writer = w;
    this.iwriter = iw;
    rb = ResourceBundle.getBundle("ContentErrorMessages");
    this.placeholderImageBytes = new BlobBytes(ir.readPlaceholderImage());
  }

  @Override
  public Content getByID(ContentID x) {
    return reader.getByID(x);
  }

  @Override
  public List<Content> findByMemberID(MemberID x) {
    return reader.findByMemberID(x);
  }

  @Override
  public List<Event> handle(BaseContentCommand x) {
    var ys =
      switch (x) {
        case CreatePhotoDiary x1 -> handleCreatePhotoDiary(x1);
        case UploadPhoto x1 -> handleUploadPhoto(x1);
        case DeleteContent x1 -> handleDeleteContent(x1);
      };
    logEvents(ys);
    return ys;
  }

  private List<Event> handleDeleteContent(DeleteContent cmd) {
    MemberID mid = validateMemberLoggedIn(cmd);
    Content c = validateContentOwnedByMember(cmd.contentID(), mid);
    FileName std = new FileName(c.content().asString());
    iwriter.delete(mid, std);
    FileName orig = new FileName(
      std.val().replace("." + STD_IMAGE + ".", "." + ORIGINAL_IMAGE + ".")
    );
    iwriter.delete(mid, orig);
    writer.delete(cmd.contentID());
    return List.of(new ContentDeleted(cmd));
  }

  @Override
  public List<Content> findChildrenOfID(ContentID x) {
    return reader.findChildrenOfID(x);
  }

  private List<Event> handleUploadPhoto(UploadPhoto x) {
    // Validations
    MemberID mid = validateMemberLoggedIn(x);
    validateContentOwnedByMember(x.parent(), mid);

    CreatedOn now = new CreatedOn(this.clock.instant());
    List<Event> ys = new ArrayList<>();

    // Save the full-size image to disk as "orig".
    FileName orig = x.fileName().insertSuffix(ORIGINAL_IMAGE);
    FileName std = x.fileName().insertSuffix(STD_IMAGE);

    // Copy a "resizing now ..." a placeholder image as "std".
    // A cronjob will overwrite the placeholder with a resized version.
    iwriter.save(mid, placeholderImageBytes, std);

    // Write the original file for resizing after the placeholder
    // in case
    iwriter.save(mid, x.photoBytes(), orig);

    // Save the image as a new content item.
    NewContent newImage = new NewContent(
      x.parent(),
      x.subject().memberID(),
      x.title(x.tz()),
      now,
      new FromDateTime(x.takenOn().val()),
      new UptoDateTime(x.takenOn().val()),
      ContentType.PHOTO,
      x.orientation() == ImageOrientation.PORTRAIT
        ? BlobType.IMAGE_PORTRAIT_HREF
        : BlobType.IMAGE_LANDSCAPE_HREF,
      new BlobBytes(std.val().getBytes(StandardCharsets.UTF_8))
    );
    ContentID newImageID = writer.save(newImage);
    ys.add(new ContentCreated(newImage, newImageID));

    return ys;
  }

  private void logEvents(List<Event> ys) {
    System.out.println(ys);
  }

  static Path calculatePlaceholderImagePath(FileName x) {
    if (x == null) {
      return null;
    }
    String y = x
      .val()
      .replaceFirst("/web/static/members/.*", "/web/static/img/placeholder.png");
    return Paths.get(y);
  }

  private List<Event> handleCreatePhotoDiary(CreatePhotoDiary cmd) {
    MemberID mid = validateMemberLoggedIn(cmd);
    validateUniqueTitleForPhotoDiary(mid, cmd.title());
    CreatedOn now = new CreatedOn(this.clock.instant());

    // Add diary.
    NewContent x = new NewContent(
      null,
      cmd.subject().memberID(),
      cmd.title(),
      now,
      new FromDateTime(now.val()),
      null,
      ContentType.PHOTO_DIARY,
      BlobType.MARKDOWN,
      null
    );
    ContentID diaryID = writer.save(x);
    Event y = new ContentCreated(x, diaryID);

    return Collections.singletonList(y);
  }

  MemberID validateMemberLoggedIn(Command x) {
    return Optional
      .ofNullable(x)
      .map(Command::subject)
      .map(Subject::memberID)
      .orElseThrow(() -> new NotAuthorizedException(rb.getString("not_logged_in")));
  }

  private Content validateContentOwnedByMember(ContentID x1, MemberID x2) {
    Content y = reader.getByID(x1);
    if (!y.createdBy().equals(x2)) {
      throw new NotAuthorizedException(rb.getString("not_content_owner"));
    }
    return y;
  }

  void validateUniqueTitleForPhotoDiary(MemberID x1, Title x2) {
    Optional<Content> doc = reader
      .findByMemberID(x1)
      .stream()
      .filter(o -> o.title().equals(x2))
      .filter(o -> o.contentType().equals(ContentType.PHOTO_DIARY))
      .findFirst();
    if (doc.isPresent()) {
      throw new ValidationException(rb.getString("title_exists"));
    }
  }
}
