package com.acsredux.auth.services;

import com.acsredux.auth.CommandHandler;
import com.acsredux.auth.commands.SignUpCommand;
import com.acsredux.base.Command;
import com.acsredux.base.Event;
import com.acsredux.base.ValidationException;

public final class CommandHandlerImpl implements CommandHandler {

  @Override
  public Event handle(Command x) {
    if (x instanceof SignUpCommand c) {
      return handleSignUpCommand(c);
    } else {
      throw new IllegalArgumentException("invalid command " + x);
    }
  }

  Event handleSignUpCommand(SignUpCommand x) {
    if (x.email() == null || x.email().isBlank()) {
      throw new ValidationException("email is required");
    }
    System.out.println("handling SignUpCommand");
    return null;
  }
}
