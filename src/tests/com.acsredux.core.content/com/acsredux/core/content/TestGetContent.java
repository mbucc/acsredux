package com.acsredux.core.content;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.events.PhotoDiaryCreated;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.services.ContentServiceProvider;
import com.acsredux.core.content.values.DiaryYear;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestGetContent {

  private final Instant clockTime = Instant.now();

  private ContentService svc;
  private ContentReader r;
  private ContentWriter w;

  @BeforeEach
  void setup() {
    InstantSource c = InstantSource.fixed(this.clockTime);
    r = (ContentReader) MockProxy.of(new MockContentReader());
    w = (ContentWriter) MockProxy.of(new MockContentWriter());
    ImageWriter iw = null;
    this.svc = new ContentServiceProvider(c, r, w, iw);
  }

  @Test
  void testGetContent() {
    // execute
    var y = svc.getByID(TEST_CONTENT_ID);

    // verify
    assertEquals(TEST_PHOTO_DIARY, y);
    MockProxy.toProxy(r).assertCallCount(1).assertCall(0, "getContent", TEST_CONTENT_ID);
  }

  {
    r = (ContentReader) MockProxy.of(new MockContentReader());
    w = (ContentWriter) MockProxy.of(new MockContentWriter());
    ImageWriter iw = null;
    this.svc = new ContentServiceProvider(null, r, w, iw);
  }

  @Test
  void testCreateContent() {
    // execute
    var cmd = new CreatePhotoDiary(TEST_SUBJECT, new DiaryYear(2000), null);
    List<Event> ys = svc.handle(cmd);

    // verify
    var expected = new PhotoDiaryCreated(cmd, TEST_CONTENT_ID);
    assertEquals(1, ys.size());
    if (ys.get(0) instanceof PhotoDiaryCreated y1) {
      assertEquals(expected, y1);
    } else {
      fail("Wrong event type returned by create: " + ys.get(0));
    }
    MockProxy.toProxy(w).assertCallCount(1).assertCall(0, "createContent", cmd);
  }
}
