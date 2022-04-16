package com.acsredux.adapter.web.common;

import static com.acsredux.adapter.web.MockHttpExchange.TEST_HTTP_PRINCIPAL;
import static com.acsredux.adapter.web.common.WebUtil.CONTENT_TYPE;
import static com.acsredux.adapter.web.common.WebUtil.getContentType;
import static com.acsredux.adapter.web.common.WebUtil.getContentTypeFromString;
import static com.acsredux.adapter.web.common.WebUtil.getHeaderParameter;
import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.adapter.web.MockHttpExchange;
import com.acsredux.core.base.Command;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class TestWebUtil {

  @Test
  void testFormToAddMemberCommand() {
    // setup
    FormData x = new FormData();
    x.add("email", TEST_EMAIL.val());
    x.add("firstName", TEST_FIRST_NAME.val());
    x.add("lastName", TEST_LAST_NAME.val());
    x.add("zip", TEST_ZIP_CODE.val());
    x.add("pwd1", new String(TEST_CLEAR_TEXT_PASSWORD.val()));
    x.add("pwd2", new String(TEST_CLEAR_TEXT_PASSWORD.val()));
    x.add("command", FormCommand.CREATE.name());

    // execute
    Command y = WebUtil.form2cmd(TEST_HTTP_PRINCIPAL, x);

    // verify
    if (y instanceof CreateMember y1) {
      assertEquals(TEST_EMAIL, y1.email());
      assertEquals(TEST_FIRST_NAME, y1.firstName());
      assertEquals(TEST_LAST_NAME, y1.lastName());
      assertEquals(TEST_ZIP_CODE, y1.zipCode());
      assertArrayEquals(TEST_CLEAR_TEXT_PASSWORD.val(), y1.password1().val());
      assertArrayEquals(TEST_CLEAR_TEXT_PASSWORD.val(), y1.password2().val());
    } else {
      fail("wrong command returned");
    }
  }

  @Test
  void testFormToLoginCommand() {
    // setup
    FormData x = new FormData();
    x.add("email", TEST_EMAIL.val());
    x.add("pwd", new String(TEST_CLEAR_TEXT_PASSWORD.val()));
    x.add("command", FormCommand.LOGIN.name());

    // execute
    Command y = WebUtil.form2cmd(TEST_HTTP_PRINCIPAL, x);

    // verify
    if (y instanceof LoginMember y1) {
      assertEquals(TEST_EMAIL, y1.email());
      assertArrayEquals(TEST_CLEAR_TEXT_PASSWORD.val(), y1.password().val());
    } else {
      fail("wrong command returned");
    }
  }

  @Test
  void testGetHeaderParameterSunny() {
    assertEquals(
      "--example",
      getHeaderParameter("multipart/form-data; boundary=--example", "boundary")
    );
  }

  @Test
  void testGetHeaderParameterTrimsQuotesFromValue() {
    assertEquals(
      "my-file.txt",
      getHeaderParameter(
        "Content-Disposition: form-data; name=\"picker\"; filename=\"my-file.txt\"",
        "filename"
      )
    );
  }

  @Test
  void testGetHeaderParameterWithAdditionalParameter() {
    // Charset is a nonsense parameter here but the test is valid.
    assertEquals(
      "--example",
      getHeaderParameter(
        "multipart/form-data; boundary=--example   ; charset=us-ascii",
        "boundary"
      )
    );
  }

  @Test
  void testGetHeaderParameterFail() {
    var y = assertThrows(
      NoSuchElementException.class,
      () -> getHeaderParameter("application/x-www-form-urlencoded", "boundary")
    );
    assertEquals(
      "no boundary in header 'application/x-www-form-urlencoded'",
      y.getMessage()
    );
  }

  @Test
  void testGetContentTypeSunny() {
    // setup
    var ct = "application/x-www-form-urlencoded";
    var x = new MockHttpExchange("/");
    x.getRequestHeaders().add(CONTENT_TYPE, ct);

    // execute & verify
    assertEquals(ct, getContentType(x));
  }

  @Test
  void testGetContentTypeReturnsEmptyStringIfNoContentType() {
    // setup
    var x = new MockHttpExchange("/");
    x.getRequestHeaders().remove(CONTENT_TYPE);

    // execute
    var y = assertDoesNotThrow(() -> getContentType(x));

    // verify
    assertEquals("", y);
  }

  @Test
  void testGetContentTypeFromStringSunny() {
    assertEquals("image/jpeg", getContentTypeFromString("CONTENT-Type: image/jpeg"));
  }

  @Test
  void testGetContentTypeFromStringSunnyWithProperty() {
    assertEquals(
      "image/jpeg",
      getContentTypeFromString("lk  a;sd alskj a; Content-Type: image/jpeg; charset=abc")
    );
  }

  @Test
  void testGetContentTypeFromStringNotFound() {
    var y = assertThrows(
      NoSuchElementException.class,
      () -> getContentTypeFromString("foobar")
    );
    assertEquals("no content type in 'foobar'", y.getMessage());
  }
}
