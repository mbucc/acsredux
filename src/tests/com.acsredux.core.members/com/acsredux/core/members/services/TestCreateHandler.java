package com.acsredux.core.members.services;

import static com.acsredux.core.members.MemberService.ANONYMOUS_USERNAME;
import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestCreateHandler {

  private static final ResourceBundle MSGS = ResourceBundle.getBundle(
    "MemberErrorMessages"
  );
  private final CreatedOn clockTime = new CreatedOn(Instant.now());
  private CreateHandler service;
  private MemberReader reader;
  private MemberAdminReader adminReader;
  private MemberWriter writer;

  @BeforeEach
  void setup() {
    reader = (MemberReader) MockProxy.of(new MockMemberReader());
    adminReader =
      (MemberAdminReader) MockProxy.of(new MockMemberAdminReader(TEST_SITE_INFO));
    writer = (MemberWriter) MockProxy.of(new MockMemberWriter());
    MemberNotifier notifier = (MemberNotifier) MockProxy.of(new MockMemberNotifier());
    InstantSource clock = InstantSource.fixed(this.clockTime.val());
    this.service = new CreateHandler(reader, adminReader, writer, notifier, clock);
  }

  @ParameterizedTest
  @EnumSource
  void requiredRecordFields(RequiredFieldsTestData x) {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.validateAddMember(x.cmd)
    );

    // validate
    assertEquals(x.expected, e.getMessage());
  }

  @Test
  void testCheckEmailUnique() {
    // setup
    //    given(reader.findByEmail(TEST_EMAIL)).willReturn(Optional.of(TEST_MEMBER));

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkEmailIsUnique(TEST_EMAIL)
    );

    // verify
    assertEquals(MSGS.getString("email_taken"), e.getMessage());
  }

  @Test
  void testCheckNameUnique() {
    // setup
    //    given(reader.findByName(TEST_FIRST_NAME, TEST_LAST_NAME))
    //      .willReturn(Optional.of(TEST_MEMBER));

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkNameIsUnique(TEST_FIRST_NAME, TEST_LAST_NAME)
    );

    // verify
    assertEquals(MSGS.getString("name_taken"), e.getMessage());
  }

  @Test
  void testCheckNameNotAnonymousUsername() {
    // setup
    // Note that neither FirstName or LastName constructors accept a blank
    // string, so this one test case is sufficient.
    FirstName x1 = FirstName.of(ANONYMOUS_USERNAME.split(" ")[0].trim());
    LastName x2 = LastName.of(ANONYMOUS_USERNAME.split(" ")[1].trim());

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkNameIsNotAnonymousUsername(x1, x2)
    );

    // verify
    assertEquals(MSGS.getString("name_is_anonymous_username"), e.getMessage());
  }

  @Test
  void testSunnyPath() {
    // execute
    assertDoesNotThrow(() -> service.handle(TEST_ADD_MEMBER2_CMD));

    // verify
    MockProxy
      .toProxy(writer)
      .assertCallCount(2)
      .assertCall(
        0,
        "addMember",
        TEST_ADD_MEMBER2_CMD,
        MemberStatus.NEEDS_EMAIL_VERIFICATION,
        this.clockTime
      )
      .assertCall(1, "addAddMemberToken", TEST_MEMBER_ID, this.clockTime);

    MockProxy
      .toProxy(reader)
      .assertCallCount(2)
      .assertCall(0, "findByEmail", TEST_ADD_MEMBER2_CMD.email())
      .assertCall(
        1,
        "findByName",
        TEST_ADD_MEMBER2_CMD.firstName(),
        TEST_ADD_MEMBER2_CMD.lastName()
      );

    MockProxy.toProxy(adminReader).assertCallCount(1).assertCall(0, "getSiteInfo");
    //    MemberAdded memberAdded = new MemberAdded(
    //            TEST_ADD_MEMBER2_CMD,
    //            this.clockTime,
    //            TEST_VERIFICATION_TOKEN,
    //            TEST_MEMBER_ID
    //    );
    //    then(notifier).should().memberAdded(memberAdded, TEST_SITE_INFO);
    //    then(notifier).shouldHaveNoMoreInteractions();
  }

  private enum RequiredFieldsTestData {
    EMAIL(
      new AddMember(
        TEST_SUBJECT,
        TEST_FIRST_NAME,
        TEST_LAST_NAME,
        null,
        TEST_CLEAR_TEXT_PASSWORD,
        TEST_CLEAR_TEXT_PASSWORD,
        TEST_ZIP_CODE
      ),
      MSGS.getString("email_missing")
    ),
    PASSWORD1(
      new AddMember(
        TEST_SUBJECT,
        TEST_FIRST_NAME,
        TEST_LAST_NAME,
        TEST_EMAIL,
        null,
        TEST_CLEAR_TEXT_PASSWORD,
        TEST_ZIP_CODE
      ),
      MSGS.getString("password1_missing")
    ),
    PASSWORD2(
      new AddMember(
        TEST_SUBJECT,
        TEST_FIRST_NAME,
        TEST_LAST_NAME,
        TEST_EMAIL,
        TEST_CLEAR_TEXT_PASSWORD,
        null,
        TEST_ZIP_CODE
      ),
      MSGS.getString("password2_missing")
    );

    final AddMember cmd;
    final String expected;

    RequiredFieldsTestData(AddMember cmd, String expected) {
      this.cmd = cmd;
      this.expected = expected;
    }
  }
}
