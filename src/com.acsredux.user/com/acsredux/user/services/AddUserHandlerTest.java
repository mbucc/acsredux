package com.acsredux.user.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.inOrder;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.acsredux.base.ValidationException;
import com.acsredux.base.entities.User;
import com.acsredux.base.values.ClearTextPassword;
import com.acsredux.base.values.CreatedOn;
import com.acsredux.base.values.Email;
import com.acsredux.base.values.FirstName;
import com.acsredux.base.values.GrowingZone;
import com.acsredux.base.values.LastName;
import com.acsredux.base.values.UserID;
import com.acsredux.base.values.VerificationToken;
import com.acsredux.user.commands.AddUser;
import com.acsredux.user.events.UserAdded;
import com.acsredux.user.ports.Notifier;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.ports.Writer;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;

class AddUserHandlerTest {

  public static final Email TEST_EMAIL = new Email("test@example.com");

  private AddUserHandler service;
  private Reader reader;
  private Writer writer;
  private Notifier notifier;
  private InstantSource clock;
  private CreatedOn clockTime = new CreatedOn(Instant.now());

  @BeforeEach
  void setup() {
    this.reader = mock(Reader.class);
    this.writer = mock(Writer.class);
    this.notifier = mock(Notifier.class);
    this.clock = InstantSource.fixed(this.clockTime.val());
    this.service =
      new AddUserHandler(this.reader, this.writer, this.notifier, this.clock);
  }

  private static enum RequiredFieldsTestData {
    EMAIL(
      new AddUser(
        new FirstName("first"),
        new LastName("last"),
        null,
        new ClearTextPassword("a3cDefg!"),
        null
      )
    ),
    PASSWORD(
      new AddUser(
        new FirstName("first"),
        new LastName("last"),
        new Email("t@t.com"),
        null,
        null
      )
    );

    AddUser cmd;

    RequiredFieldsTestData(AddUser cmd) {
      this.cmd = cmd;
    }
  }

  @ParameterizedTest
  @EnumSource
  void requiredRecordFields(RequiredFieldsTestData x) {
    // execute
    var e = assertThrows(ValidationException.class, () -> service.validateAddUser(x.cmd));

    // validate
    String fld = x.name().toLowerCase().replace('_', ' ');
    assertEquals(fld + " is required", e.getMessage());
  }

  @Test
  void testCheckEmailUnique() {
    // setup
    given(reader.findByEmail(TEST_EMAIL)).willReturn(testUser());

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkEmailIsUnique(TEST_EMAIL)
    );

    // verify
    assertEquals("email already on file", e.getMessage());
  }

  Optional<User> testUser() {
    return Optional.of(new User(TEST_EMAIL));
  }

  @Test
  void testSunnyPath() {
    // setup
    AddUser cmd = new AddUser(
      new FirstName("first"),
      new LastName("last"),
      new Email("email"),
      new ClearTextPassword("a3cDefg!"),
      new GrowingZone("zone")
    );
    UserID newUserID = new UserID(123L);
    VerificationToken token = new VerificationToken("test token");
    UserAdded userAdded = new UserAdded(cmd, this.clockTime, token);
    given(reader.findByEmail(TEST_EMAIL)).willReturn(Optional.empty());
    given(writer.addUser(cmd, this.clockTime)).willReturn(newUserID);
    given(writer.addAddUserToken(newUserID, this.clockTime)).willReturn(token);

    // execute
    UserAdded y = assertDoesNotThrow(() -> service.handle(cmd));

    // verify
    then(reader).should().findByEmail(new Email("email"));
    then(writer).should().addUser(cmd, clockTime);
    then(writer).should().addAddUserToken(newUserID, clockTime);
    then(reader).shouldHaveNoMoreInteractions();
    then(writer).shouldHaveNoMoreInteractions();
    then(notifier).should().userAdded(userAdded);
    then(notifier).shouldHaveNoMoreInteractions();
  }
}
