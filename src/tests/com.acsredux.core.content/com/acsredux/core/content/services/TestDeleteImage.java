package com.acsredux.core.content.services;

import static com.acsredux.lib.testutil.TestData.TEST_DELETE_CONTENT_COMMAND;
import static com.acsredux.lib.testutil.TestData.TEST_IMAGE_ORIG_FILE_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_IMAGE_STD_FILE_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_PHOTO_CONTENT_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.MockContentReader;
import com.acsredux.core.content.MockContentWriter;
import com.acsredux.core.content.MockImageReader;
import com.acsredux.core.content.MockImageWriter;
import com.acsredux.core.content.events.ContentDeleted;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageReader;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestDeleteImage {

  private final Instant clockTime = Instant.now();

  ContentReader r = (ContentReader) MockProxy.of(new MockContentReader());
  ContentWriter w = (ContentWriter) MockProxy.of(new MockContentWriter());
  ImageReader ir = (ImageReader) MockProxy.of(new MockImageReader());
  ImageWriter iw = (ImageWriter) MockProxy.of(new MockImageWriter());
  InstantSource c = InstantSource.fixed(this.clockTime);
  ContentServiceProvider service = new ContentServiceProvider(c, r, w, ir, iw);

  @Test
  void testDeleteImage() {
    // execute
    var ys = assertDoesNotThrow(() -> service.handle(TEST_DELETE_CONTENT_COMMAND));

    // verify calls made
    MockProxy
      .toProxy(r)
      .assertCallCount(1)
      .assertCall(0, "getByID", TEST_PHOTO_CONTENT_ID);
    MockProxy
      .toProxy(iw)
      .assertCallCount(2)
      .assertCall(0, "delete", TEST_MEMBER_ID, TEST_IMAGE_STD_FILE_NAME)
      .assertCall(1, "delete", TEST_MEMBER_ID, TEST_IMAGE_ORIG_FILE_NAME);
    MockProxy
      .toProxy(w)
      .assertCallCount(1)
      .assertCall(0, "delete", TEST_PHOTO_CONTENT_ID);
    List<Event> expectedEvents = List.of(new ContentDeleted(TEST_DELETE_CONTENT_COMMAND));
    assertEquals(expectedEvents, ys);
  }
}
