package com.acsredux.core.members.commands;

import static com.acsredux.lib.testutil.TestData.TEST_CLEAR_TEXT_PASSWORD;
import static com.acsredux.lib.testutil.TestData.TEST_EMAIL;
import static com.acsredux.lib.testutil.TestData.TEST_FIRST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_LAST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_SUBJECT;
import static com.acsredux.lib.testutil.TestData.TEST_ZIP_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;

public class TestCreateMember {

  private static final ResourceBundle MSGS = ResourceBundle.getBundle(
    "MemberErrorMessages"
  );

  @Test
  void subjectRequired() {
    // execute
    var e = assertThrows(
      IllegalStateException.class,
      () ->
        new CreateMember(
          null,
          TEST_FIRST_NAME,
          TEST_LAST_NAME,
          TEST_EMAIL,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_ZIP_CODE
        )
    );

    // validate
    assertEquals("Subject null", e.getMessage());
  }

  @Test
  void firstNameRequired() {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () ->
        new CreateMember(
          TEST_SUBJECT,
          null,
          TEST_LAST_NAME,
          TEST_EMAIL,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_ZIP_CODE
        )
    );

    // validate
    assertEquals(MSGS.getString("firstname_missing"), e.getMessage());
  }

  @Test
  void lastNameRequired() {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () ->
        new CreateMember(
          TEST_SUBJECT,
          TEST_FIRST_NAME,
          null,
          TEST_EMAIL,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_ZIP_CODE
        )
    );

    // validate
    assertEquals(MSGS.getString("lastname_missing"), e.getMessage());
  }

  @Test
  void emailRequired() {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () ->
        new CreateMember(
          TEST_SUBJECT,
          TEST_FIRST_NAME,
          TEST_LAST_NAME,
          null,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_ZIP_CODE
        )
    );

    // validate
    assertEquals(MSGS.getString("email_missing"), e.getMessage());
  }

  @Test
  void password1Required() {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () ->
        new CreateMember(
          TEST_SUBJECT,
          TEST_FIRST_NAME,
          TEST_LAST_NAME,
          TEST_EMAIL,
          null,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_ZIP_CODE
        )
    );

    // validate
    assertEquals(MSGS.getString("password1_missing"), e.getMessage());
  }

  @Test
  void password2Required() {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () ->
        new CreateMember(
          TEST_SUBJECT,
          TEST_FIRST_NAME,
          TEST_LAST_NAME,
          TEST_EMAIL,
          TEST_CLEAR_TEXT_PASSWORD,
          null,
          TEST_ZIP_CODE
        )
    );

    // validate
    assertEquals(MSGS.getString("password2_missing"), e.getMessage());
  }

  @Test
  void zipCodeRequired() {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () ->
        new CreateMember(
          TEST_SUBJECT,
          TEST_FIRST_NAME,
          TEST_LAST_NAME,
          TEST_EMAIL,
          TEST_CLEAR_TEXT_PASSWORD,
          TEST_CLEAR_TEXT_PASSWORD,
          null
        )
    );

    // validate
    assertEquals(MSGS.getString("zipcode_missing"), e.getMessage());
  }
}
