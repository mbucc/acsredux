package com.acsredux.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.auth.CommandHandler;
import com.acsredux.auth.Factory;
import com.acsredux.auth.commands.SignUpCommand;
import com.acsredux.base.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SignUpCommandTest {

  private CommandHandler service;

  @BeforeEach
  void setup() {
    this.service = Factory.getCommandHandler();
  }

  @Test
  void emailIsRequired() {
    // setup
    var cmd = new SignUpCommand("first", "last", null, "pwd", "5A", "mark");

    // execute
    var e = assertThrows(ValidationException.class, () -> service.handle(cmd));

    // validate
    assertEquals("email is required", e.getMessage());
  }
}
