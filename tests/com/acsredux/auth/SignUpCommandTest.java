package com.acsredux.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.auth.CommandHandler;
import com.acsredux.auth.Factory;
import com.acsredux.auth.commands.SignUpCommand;
import com.acsredux.base.Command;
import com.acsredux.base.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SignUpCommandTest {

  private CommandHandler service;

  @BeforeEach
  void setup() {
    this.service = Factory.getCommandHandler();
  }

  private static enum RequiredFieldsTestData {
    EMAIL(new SignUpCommand("first", "last", null, "pass", "5A", "mark"));

    Command cmd;

    RequiredFieldsTestData(Command cmd) {
      this.cmd = cmd;
    }
  }

  @ParameterizedTest
  @EnumSource
  void requiredRecordFields(RequiredFieldsTestData x) {
    // execute
    var e = assertThrows(
      ValidationException.class,
      () -> service.handle(x.cmd)
    );

    // validate
    String fld = x.name().toLowerCase().replace('_', ' ');
    assertEquals(fld + " is required", e.getMessage());
  }
}
