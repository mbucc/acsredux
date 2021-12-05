package com.acsredux.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.acsredux.auth.commands.SignUpCommand;
import com.acsredux.auth.ports.Reader;
import com.acsredux.base.ValidationException;
import com.acsredux.base.entities.User;
import com.acsredux.base.values.Email;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SignUpCommandHandlerSignUpTest {

  public static final String TEST_EMAIL = "test@example.com";

  private CommandHandlerImpl service;
  private Reader db;

  @BeforeEach
  void setup() {
    this.db = mock(Reader.class);
    this.service = new CommandHandlerImpl(this.db);
    //this.service = new CommandHandlerImpl(null);
  }

  private static enum RequiredFieldsTestData {
    EMAIL(new SignUpCommand("first", "last", null, "pass", null, null)),
    FIRST_NAME(new SignUpCommand(null, "last", "t@t.com", "pass", null, null)),
    LAST_NAME(new SignUpCommand("first", null, "t@t.com", "pass", null, null)),
    PASSWORD(new SignUpCommand("first", "last", "t@t.com", null, null, null));

    SignUpCommand cmd;

    RequiredFieldsTestData(SignUpCommand cmd) {
      this.cmd = cmd;
    }
  }

  @ParameterizedTest
  @EnumSource
  void requiredRecordFields(RequiredFieldsTestData x) {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.validateSignUpCommand(x.cmd)
    );

    // validate
    String fld = x.name().toLowerCase().replace('_', ' ');
    assertEquals(fld + " is required", e.getMessage());
  }

  @Test
  void testCheckEmailUnique() {
    // setup
    Email email = new Email(TEST_EMAIL);
    given(db.findByEmail(email)).willReturn(testUser());

    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.checkEmailIsUnique(email)
    );

    // verify
    assertEquals("email already on file", e.getMessage());
  }

  Optional<User> testUser() {
    return Optional.of(new User(new Email(TEST_EMAIL)));
  }
}
