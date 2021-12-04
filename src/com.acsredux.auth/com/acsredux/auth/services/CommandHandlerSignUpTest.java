package com.acsredux.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.auth.commands.SignUpCommand;
import com.acsredux.base.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SignUpCommandHandlerSignUpTest {

  private CommandHandlerImpl service;

  @BeforeEach
  void setup() {
    this.service = new CommandHandlerImpl();
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
}
