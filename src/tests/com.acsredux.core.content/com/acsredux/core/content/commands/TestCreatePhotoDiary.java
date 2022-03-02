package com.acsredux.core.content.commands;

import static com.acsredux.lib.testutil.TestData.TEST_SUBJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.core.content.values.DiaryName;
import com.acsredux.core.content.values.DiaryYear;
import com.acsredux.core.content.values.Title;
import org.junit.jupiter.api.Test;

public class TestCreatePhotoDiary {

  @Test
  void testTitleWithNoName() {
    var cmd = new CreatePhotoDiary(TEST_SUBJECT, new DiaryYear(2022), null);
    assertEquals(Title.of("2022"), cmd.title());
  }

  @Test
  void testTitleWithName() {
    var cmd = new CreatePhotoDiary(
      TEST_SUBJECT,
      new DiaryYear(2022),
      new DiaryName("foo")
    );
    assertEquals(Title.of("2022: foo"), cmd.title());
  }
}
