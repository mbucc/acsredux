package com.acsredux.members.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.base.ValidationException;
import com.acsredux.members.commands.AddMember;
import com.acsredux.members.entities.Member;
import com.acsredux.members.events.MemberAdded;
import com.acsredux.members.ports.Notifier;
import com.acsredux.members.ports.Reader;
import com.acsredux.members.ports.Writer;
import com.acsredux.members.values.*;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Optional;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AddMemberHandlerTest {

  public static final Email TEST_EMAIL = new Email("test@example.com");

  private AddMemberHandler service;
  private Reader reader;
  private Writer writer;
  private Notifier notifier;
  private InstantSource clock;
  private CreatedOn clockTime = new CreatedOn(Instant.now());
  private static ResourceBundle msgs = ResourceBundle.getBundle("MemberErrorMessages");

  @BeforeEach
  void setup() {
    this.reader = mock(Reader.class);
    this.writer = mock(Writer.class);
    this.notifier = mock(Notifier.class);
    this.clock = InstantSource.fixed(this.clockTime.val());
    this.service =
      new AddMemberHandler(this.reader, this.writer, this.notifier, this.clock);
  }

  private static enum RequiredFieldsTestData {
    EMAIL(
      new AddMember(
        new FirstName("first"),
        new LastName("last"),
        null,
        new ClearTextPassword("a3cDefg!"),
        new ClearTextPassword("a3cDefg!"),
        null
      ),
      msgs.getString("email_missing")
    ),
    PASSWORD1(
      new AddMember(
        new FirstName("first"),
        new LastName("last"),
        new Email("t@t.com"),
        null,
        new ClearTextPassword("a3cDefg!"),
        null
      ),
      msgs.getString("password1_missing")
    ),
    PASSWORD2(
      new AddMember(
        new FirstName("first"),
        new LastName("last"),
        new Email("t@t.com"),
        new ClearTextPassword("a3cDefg!"),
        null,
        null
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
    given(reader.findByEmail(TEST_EMAIL)).willReturn(testMember());

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkEmailIsUnique(TEST_EMAIL)
    );

    // verify
    assertEquals(msgs.getString("email_taken"), e.getMessage());
  }

  Optional<Member> testMember() {
    return Optional.of(
      new Member(
        new MemberID(1L),
        TEST_EMAIL,
        new FirstName("f"),
        new LastName("l"),
        new GrowingZone("5A")
      )
    );
  }

  @Test
  void testSunnyPath() {
    // setup
    AddMember cmd = new AddMember(
      new FirstName("first"),
      new LastName("last"),
      new Email("email"),
      new ClearTextPassword("a3cDefg!"),
      new ClearTextPassword("a3cDefg!"),
      new GrowingZone("zone")
    );
    MemberID newMemberID = new MemberID(123L);
    VerificationToken token = new VerificationToken("test token");
    MemberAdded memberAdded = new MemberAdded(cmd, this.clockTime, token, newMemberID);
    given(reader.findByEmail(TEST_EMAIL)).willReturn(Optional.empty());
    given(writer.addMember(cmd, this.clockTime)).willReturn(newMemberID);
    given(writer.addAddMemberToken(newMemberID, this.clockTime)).willReturn(token);

    // execute
    MemberAdded y = assertDoesNotThrow(() -> service.handle(cmd));

    // verify
    then(reader).should().findByEmail(new Email("email"));
    then(writer).should().addMember(cmd, clockTime);
    then(writer).should().addAddMemberToken(newMemberID, clockTime);
    then(reader).shouldHaveNoMoreInteractions();
    then(writer).shouldHaveNoMoreInteractions();
    then(notifier).should().memberAdded(memberAdded);
    then(notifier).shouldHaveNoMoreInteractions();
  }
}
