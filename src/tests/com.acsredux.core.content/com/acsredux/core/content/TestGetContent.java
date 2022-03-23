package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.events.ContentCreated;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageReader;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.services.ContentServiceProvider;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TestGetContent {

  private final Instant clockTime = Instant.now();

  private ContentService svc;
  private ContentReader r;
  private ContentWriter w;
  private ImageReader ir;
  private ImageWriter iw;

  @BeforeEach
  void setup() {
    InstantSource c = InstantSource.fixed(this.clockTime);
    r = (ContentReader) MockProxy.of(new MockContentReader());
    w = (ContentWriter) MockProxy.of(new MockContentWriter());
    ir = (ImageReader) MockProxy.of(new MockImageReader());
    iw = (ImageWriter) MockProxy.of(new MockImageWriter());
    this.svc = new ContentServiceProvider(c, r, w, ir, iw);
  }

  @Test
  @Disabled("refactoring content")
  void testGetContent() {
    // execute
    var y = svc.getPhotoDiaryByID(TEST_DIARY_CONTENT_ID);

    // verify
    assertEquals(TEST_PHOTO_DIARY_MAIN_CONTENT, y);
    MockProxy
      .toProxy(r)
      .assertCallCount(1)
      .assertCall(0, "getByID", TEST_DIARY_CONTENT_ID);
  }

  {
    r = (ContentReader) MockProxy.of(new MockContentReader());
    w = (ContentWriter) MockProxy.of(new MockContentWriter());
    ir = (ImageReader) MockProxy.of(new MockImageReader());
    iw = (ImageWriter) MockProxy.of(new MockImageWriter());
    this.svc = new ContentServiceProvider(null, r, w, ir, iw);
  }

  @Test
  void testCreateContent() {
    // execute
    var cmd = new CreatePhotoDiary(TEST_SUBJECT, new DiaryYear(2000), null);
    List<Event> ys = svc.handle(cmd);

    // verify
    if (1 != ys.size()) {
      ys.forEach(System.out::println);
      fail("wrong number of events returned");
    }
    if (ys.get(0) instanceof ContentCreated y0) {
      var newContent = new NewContent(
        null,
        cmd.subject().memberID(),
        cmd.title(),
        new CreatedOn(clockTime),
        new FromDateTime(clockTime),
        null,
        ContentType.PHOTO_DIARY,
        BlobType.MARKDOWN,
        null
      );
      var expected = new ContentCreated(newContent, TEST_DIARY_CONTENT_ID);
      assertEquals(expected, y0);
      MockProxy.toProxy(w).assertCallCount(1).assertCall(0, "save", newContent);
    } else {
      fail("Wrong event type returned by create: " + ys.get(0));
    }
  }
}
