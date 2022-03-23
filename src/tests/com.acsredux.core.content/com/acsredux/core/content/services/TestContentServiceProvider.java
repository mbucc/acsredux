package com.acsredux.core.content.services;

import static com.acsredux.core.content.values.ContentType.PHOTO_DIARY;
import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.base.Subject;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.MockContentReader;
import com.acsredux.core.content.MockContentWriter;
import com.acsredux.core.content.MockImageReader;
import com.acsredux.core.content.MockImageWriter;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageReader;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.lib.testutil.MockProxy;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.InstantSource;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

public class TestContentServiceProvider {

  private final Instant clockTime = Instant.now();

  ContentReader r = (ContentReader) MockProxy.of(new MockContentReader());
  ContentWriter w = (ContentWriter) MockProxy.of(new MockContentWriter());
  ImageReader ir = (ImageReader) MockProxy.of(new MockImageReader());
  ImageWriter iw = (ImageWriter) MockProxy.of(new MockImageWriter());
  InstantSource c = InstantSource.fixed(this.clockTime);
  ContentServiceProvider service = new ContentServiceProvider(c, r, w, ir, iw);
  ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");

  @Test
  void testThatFindByMemberIDIsCalled() {
    // execute
    assertThrows(
      ValidationException.class,
      () -> service.handle(TEST_CREATE_PHOTO_DIARY_COMMAND)
    );

    // verify
    MockProxy
      .toProxy(r)
      .assertCallCount(1)
      .assertCall(0, "findByMemberID", TEST_MEMBER_ID);
  }

  @Test
  void testValidateUniqueTitleByMember() {
    var y = assertThrows(
      ValidationException.class,
      () ->
        service.validateUniqueTitleForMemberAndContentType(
          TEST_MEMBER_ID,
          PHOTO_DIARY,
          TEST_TITLE
        )
    );
    assertEquals(rb.getString("title_exists"), y.getMessage());
  }

  @Test
  void testValidateMemberLoggedIn() {
    var x = new CreatePhotoDiary(new Subject(null), TEST_DIARY_YEAR, null);
    var y = assertThrows(
      AuthenticationException.class,
      () -> service.validateMemberLoggedIn(x)
    );
    assertEquals(rb.getString("not_logged_in"), y.getMessage());
  }

  @Test
  void testSunnyAddPhoto() {
    // execute
    var y = assertDoesNotThrow(() -> service.handle(TEST_UPLOAD_PHOTO_COMMAND));

    // verify calls made
    MockProxy
      .toProxy(iw)
      .assertCallCount(2)
      .assertCall(0, "save", TEST_MEMBER_ID, TEST_IMAGE_BLOB, TEST_IMAGE_STD_FILE_NAME)
      .assertCall(1, "save", TEST_MEMBER_ID, TEST_IMAGE_BLOB, TEST_IMAGE_ORIG_FILE_NAME);

    NewContent expectedNewContentForImage = new NewContent(
      TEST_UPLOAD_PHOTO_COMMAND.parent(),
      TEST_UPLOAD_PHOTO_COMMAND.subject().memberID(),
      TEST_UPLOAD_PHOTO_COMMAND.title(TEST_TIME_ZONE),
      new CreatedOn(clockTime),
      new FromDateTime(TEST_PHOTO_TAKEN_ON.val()),
      new UptoDateTime(TEST_PHOTO_TAKEN_ON.val()),
      ContentType.PHOTO,
      BlobType.IMAGE_PORTRAIT_HREF,
      new BlobBytes(TEST_IMAGE_STD_FILE_NAME.val().getBytes(StandardCharsets.UTF_8))
    );
    MockProxy
      .toProxy(w)
      .assertCallCount(1)
      .assertCall(0, "save", expectedNewContentForImage);
  }
}
