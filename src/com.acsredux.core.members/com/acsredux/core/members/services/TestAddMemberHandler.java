package com.acsredux.core.members.services;

import static com.acsredux.core.members.MemberService.ANONYMOUS_USERNAME;
import static com.acsredux.lib.testutil.TestData.TEST_ADD_MEMBER_CMD;
import static com.acsredux.lib.testutil.TestData.TEST_EMAIL;
import static com.acsredux.lib.testutil.TestData.TEST_FIRST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_LAST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_PASSWORD;
import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static com.acsredux.lib.testutil.TestData.TEST_TOKEN;
import static com.acsredux.lib.testutil.TestData.TEST_ZIP_CODE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Optional;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestAddMemberHandler {

  private AddMemberHandler service;
  private MemberReader reader;
  private MemberAdminReader adminReader;
  private MemberWriter writer;
  private MemberNotifier notifier;
  private InstantSource clock;
  private CreatedOn clockTime = new CreatedOn(Instant.now());
  private static ResourceBundle msgs = ResourceBundle.getBundle("MemberErrorMessages");

  @BeforeEach
  void setup() {
    this.reader = mock(MemberReader.class);
    this.adminReader = mock(MemberAdminReader.class);
    this.writer = mock(MemberWriter.class);
    this.notifier = mock(MemberNotifier.class);
    this.clock = InstantSource.fixed(this.clockTime.val());
    this.service =
      new AddMemberHandler(
        this.reader,
        this.adminReader,
        this.writer,
        this.notifier,
        this.clock
      );
  }

  private static enum RequiredFieldsTestData {
    EMAIL(
      new AddMember(
        TEST_FIRST_NAME,
        TEST_LAST_NAME,
        null,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_ZIP_CODE
      ),
      msgs.getString("email_missing")
    ),
    PASSWORD1(
      new AddMember(
        TEST_FIRST_NAME,
        TEST_LAST_NAME,
        TEST_EMAIL,
        null,
        TEST_PASSWORD,
        TEST_ZIP_CODE
      ),
      msgs.getString("password1_missing")
    ),
    PASSWORD2(
      new AddMember(
        TEST_FIRST_NAME,
        TEST_LAST_NAME,
        TEST_EMAIL,
        TEST_PASSWORD,
        null,
        TEST_ZIP_CODE
      ),
      msgs.getString("password2_missing")
    );

    AddMember cmd;
    String expected;

    RequiredFieldsTestData(AddMember cmd, String expected) {
      this.cmd = cmd;
      this.expected = expected;
    }
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
    given(reader.findByEmail(TEST_EMAIL)).willReturn(Optional.of(TEST_MEMBER));

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkEmailIsUnique(TEST_EMAIL)
    );

    // verify
    assertEquals(msgs.getString("email_taken"), e.getMessage());
  }

  @Test
  void testCheckNameUnique() {
    // setup
    given(reader.findByName(TEST_FIRST_NAME, TEST_LAST_NAME))
      .willReturn(Optional.of(TEST_MEMBER));

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkNameIsUnique(TEST_FIRST_NAME, TEST_LAST_NAME)
    );

    // verify
    assertEquals(msgs.getString("name_taken"), e.getMessage());
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
    assertEquals(msgs.getString("name_is_anonymous_username"), e.getMessage());
  }

  @Test
  void testSunnyPath() {
    // setup
    given(reader.findByEmail(TEST_EMAIL)).willReturn(Optional.empty());
    given(adminReader.getSiteInfo()).willReturn(TEST_SITE_INFO);
    given(
      writer.addMember(
        TEST_ADD_MEMBER_CMD,
        MemberStatus.NEEDS_EMAIL_VERIFICATION,
        this.clockTime
      )
    )
      .willReturn(TEST_MEMBER_ID);
    given(writer.addAddMemberToken(TEST_MEMBER_ID, this.clockTime))
      .willReturn(TEST_TOKEN);

    // execute
    MemberAdded y = assertDoesNotThrow(() -> service.handle(TEST_ADD_MEMBER_CMD));

    // verify
    MemberAdded memberAdded = new MemberAdded(
      TEST_ADD_MEMBER_CMD,
      this.clockTime,
      TEST_TOKEN,
      TEST_MEMBER_ID
    );

    then(reader).should().findByEmail(TEST_EMAIL);
    then(reader).should().findByName(TEST_FIRST_NAME, TEST_LAST_NAME);
    then(reader).shouldHaveNoMoreInteractions();

    then(adminReader).should().getSiteInfo();
    then(adminReader).shouldHaveNoMoreInteractions();

    then(writer)
      .should()
      .addMember(TEST_ADD_MEMBER_CMD, MemberStatus.NEEDS_EMAIL_VERIFICATION, clockTime);
    then(writer).should().addAddMemberToken(TEST_MEMBER_ID, clockTime);
    then(writer).shouldHaveNoMoreInteractions();

    then(notifier).should().memberAdded(memberAdded, TEST_SITE_INFO);
    then(notifier).shouldHaveNoMoreInteractions();
  }
}
