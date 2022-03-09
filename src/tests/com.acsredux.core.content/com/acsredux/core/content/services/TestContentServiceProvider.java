package com.acsredux.core.content.services;

import static com.acsredux.lib.testutil.TestData.TEST_CREATE_PHOTO_DIARY_COMMAND;
import static com.acsredux.lib.testutil.TestData.TEST_DIARY_YEAR;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.MockContentReader;
import com.acsredux.core.content.MockContentWriter;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.lib.testutil.MockProxy;
import java.util.ResourceBundle;
import javax.security.auth.Subject;
import org.junit.jupiter.api.Test;

public class TestContentServiceProvider {

  ContentReader r = (ContentReader) MockProxy.of(new MockContentReader());
  ContentWriter w = (ContentWriter) MockProxy.of(new MockContentWriter());
  ImageWriter iw = null;
  ContentServiceProvider service = new ContentServiceProvider(null, r, w, iw);
  ResourceBundle rb = ResourceBundle.getBundle("ContentErrorMessages");

  @Test
  void testThatPhotoDiaryTitleIsUniqueByMember() {
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
      () -> service.validateUniqueTitleForMember(TEST_MEMBER_ID, TEST_TITLE)
    );
    assertEquals(rb.getString("title_exists"), y.getMessage());
  }

  @Test
  void testValidateMemberLoggedIn() {
    var x = new CreatePhotoDiary(new Subject(), TEST_DIARY_YEAR, null);
    var y = assertThrows(
      AuthenticationException.class,
      () -> service.validateMemberLoggedIn(x)
    );
    assertEquals(rb.getString("not_logged_in1"), y.getMessage());
  }
}
