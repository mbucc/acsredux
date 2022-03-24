package com.acsredux.core.content.services;

import static com.acsredux.core.content.values.ContentType.PHOTO_DIARY;

import com.acsredux.core.base.*;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.ContentCreated;
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

  public static final String DIARY_FMT =
    """
        # %s
        
        ## Jan
        
        ## Feb
        
        ## Mar
        
        ## Apr
        
        ## May
        
        ## Jun
        
        ## Jul
        
        ## Aug
        
        ## Sep
        
        ## Oct
        
        ## Nov
        
        ## Dev""";

  //  private final List<String> months = List.of(
  //    "Jan",
  //    "Feb",
  //    "Mar",
  //    "Apr",
  //    "May",
  //    "Jun",
  //    "Jul",
  //    "Aug",
  //    "Sep",
  //    "Oct",
  //    "Nov",
  //    "Dec"
  //  );

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
    return switch (x) {
      case CreatePhotoDiary x1 -> handleCreatePhotoDiary(x1);
      case UploadPhoto x1 -> handleUploadPhoto(x1);
    };
  }

  private List<Event> handleUploadPhoto(UploadPhoto x) {
    // Validations
    MemberID mid = validateMemberLoggedIn(x);
    validateContentOwnedByMember(x.parent(), mid);

    CreatedOn now = new CreatedOn(this.clock.instant());
    List<Event> ys = new ArrayList<>();

    // Save the full-size image to disk as "orig".
    FileName orig = x.fileName().insertSuffix("orig");
    FileName std = x.fileName().insertSuffix("std");

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

  static Path calculatePlaceholderImagePath(FileName x) {
    if (x == null) {
      return null;
    }
    String y = x
      .val()
      .replaceFirst("/web/static/members/.*", "/web/static/img/placeholder.png");
    return Paths.get(y);
  }

  private Content insertPhoto(UploadPhoto x) {
    throw new UnsupportedOperationException("implement me");
  }

  private List<Event> handleCreatePhotoDiary(CreatePhotoDiary x) {
    MemberID mid = validateMemberLoggedIn(x);
    validateUniqueTitleForMemberAndContentType(mid, PHOTO_DIARY, x.title());

    CreatedOn now = new CreatedOn(this.clock.instant());

    // Add diary.
    NewContent y1 = new NewContent(
      null,
      x.subject().memberID(),
      x.title(),
      now,
      new FromDateTime(now.val()),
      null,
      ContentType.PHOTO_DIARY,
      BlobType.MARKDOWN,
      null
    );
    ContentID diaryID = writer.save(y1);
    Event y = new ContentCreated(y1, diaryID);

    return Collections.singletonList(y);
  }

  MemberID validateMemberLoggedIn(Command x) {
    return Optional
      .ofNullable(x)
      .map(Command::subject)
      .map(Subject::memberID)
      .orElseThrow(() -> new AuthenticationException(rb.getString("not_logged_in")));
  }

  private void validateContentOwnedByMember(ContentID x1, MemberID x2) {
    Content y = reader.getByID(x1);
    if (!y.createdBy().equals(x2)) {
      throw new AuthenticationException(rb.getString("not_content_owner"));
    }
  }

  void validateUniqueTitleForMemberAndContentType(MemberID x1, ContentType x2, Title x3) {
    Optional<Content> doc = reader
      .findByMemberID(x1)
      .stream()
      .filter(o -> o.title().equals(x3))
      .filter(o -> o.contentType().equals(x2))
      .findFirst();
    if (doc.isPresent()) {
      throw new ValidationException(rb.getString("title_exists"));
    }
  }
}
