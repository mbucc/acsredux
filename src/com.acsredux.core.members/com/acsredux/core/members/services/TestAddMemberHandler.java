package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_ADD_MEMBER_CMD;
import static com.acsredux.lib.testutil.TestData.TEST_EMAIL;
import static com.acsredux.lib.testutil.TestData.TEST_FIRST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_LAST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_PASSWORD;
import static com.acsredux.lib.testutil.TestData.TEST_SITEINFO;
import static com.acsredux.lib.testutil.TestData.TEST_TOKEN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.AdminReader;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.ports.Reader;
import com.acsredux.core.members.ports.Writer;
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
  private Reader reader;
  private AdminReader adminReader;
  private Writer writer;
  private Notifier notifier;
  private InstantSource clock;
  private CreatedOn clockTime = new CreatedOn(Instant.now());
  private static ResourceBundle msgs = ResourceBundle.getBundle("MemberErrorMessages");

  @BeforeEach
  void setup() {
    this.reader = mock(Reader.class);
    this.adminReader = mock(AdminReader.class);
    this.writer = mock(Writer.class);
    this.notifier = mock(Notifier.class);
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
      new AddMember(TEST_FIRST_NAME, TEST_LAST_NAME, null, TEST_PASSWORD, TEST_PASSWORD),
      msgs.getString("email_missing")
    ),
    PASSWORD1(
      new AddMember(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, null, TEST_PASSWORD),
      msgs.getString("password1_missing")
    ),
    PASSWORD2(
      new AddMember(TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD, null),
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
  void testSunnyPath() {
    // setup
    given(reader.findByEmail(TEST_EMAIL)).willReturn(Optional.empty());
    given(adminReader.getSiteInfo()).willReturn(TEST_SITEINFO);
    given(writer.addMember(TEST_ADD_MEMBER_CMD, this.clockTime))
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
    then(reader).shouldHaveNoMoreInteractions();

    then(adminReader).should().getSiteInfo();
    then(adminReader).shouldHaveNoMoreInteractions();

    then(writer).should().addMember(TEST_ADD_MEMBER_CMD, clockTime);
    then(writer).should().addAddMemberToken(TEST_MEMBER_ID, clockTime);
    then(writer).shouldHaveNoMoreInteractions();

    then(notifier).should().memberAdded(memberAdded, TEST_SITEINFO);
    then(notifier).shouldHaveNoMoreInteractions();
  }
}
