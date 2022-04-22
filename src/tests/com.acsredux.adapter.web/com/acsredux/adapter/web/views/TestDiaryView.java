package com.acsredux.adapter.web.views;

import static com.acsredux.adapter.web.views.DiaryView.getMonthName;
import static com.acsredux.adapter.web.views.EditPhotoView.uriToContentID;
import static com.acsredux.core.content.values.ContentType.DIARY_ENTRY;
import static com.acsredux.lib.testutil.TestData.TEST_CREATED_ON;
import static com.acsredux.lib.testutil.TestData.TEST_DIARY_CONTENT_ID;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.BlobBytes;
import com.acsredux.core.content.values.BlobType;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.FromDateTime;
import com.acsredux.core.content.values.Title;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestDiaryView {

  @Test
  void testUriToContentID() throws URISyntaxException {
    URI x = new URI("/photo-diary/123/add-image");
    assertEquals(123L, uriToContentID(x));
  }

  @Test
  void testDiaryHasOneSectionForEachMonthAndTheyAreInOrder() {
    // execute
    var ys = DiaryView.buildSections(Collections.emptyList(), ZoneId.of("US/Eastern"));

    // verify
    int i = 0;

    for (Month month : Month.values()) {
      assertEquals(getMonthName(month), ys.get(i++).title());
    }
  }

  @Test
  void testEachMonthHasNoteAsFirstChild() {
    // execute
    var ys = DiaryView.buildSections(Collections.emptyList(), ZoneId.of("US/Eastern"));

    // verify
    for (DiaryView.SectionDTO y : ys) {
      assertFalse(y.entries().isEmpty());
      assertEquals("note", y.entries().get(0).get("type"));
    }
  }

  @Test
  void testSummaryNoteOnlyAddedToTheMonthThatNeedsIt() {
    // setup
    var xs = List.of(
      buildNote(1, "Jan"),
      buildNote(2, "Feb"),
      // missing March
      buildNote(4, "Feb"),
      buildNote(5, "Feb"),
      buildNote(6, "Feb"),
      buildNote(7, "Feb"),
      buildNote(8, "Feb"),
      buildNote(9, "Feb"),
      buildNote(10, "Feb"),
      buildNote(11, "Feb"),
      buildNote(12, "Feb")
    );

    // execute
    var ys = DiaryView.buildSections(xs, ZoneId.of("US/Eastern"));

    // verify
    for (DiaryView.SectionDTO y : ys) {
      assertFalse(y.entries().isEmpty());
      assertEquals("note", y.entries().get(0).get("type"));
    }
  }

  private Content buildNote(int id, String title) {
    return new Content(
      new ContentID((long) id),
      TEST_DIARY_CONTENT_ID,
      TEST_MEMBER_ID,
      new Title(title),
      TEST_CREATED_ON,
      new FromDateTime(
        LocalDate
          .of(2022, id, 1)
          .atStartOfDay()
          .atZone(ZoneId.of("US/Eastern"))
          .toInstant()
      ),
      null,
      DIARY_ENTRY,
      BlobType.MARKDOWN,
      new BlobBytes(title.getBytes(StandardCharsets.UTF_8))
    );
  }
}
