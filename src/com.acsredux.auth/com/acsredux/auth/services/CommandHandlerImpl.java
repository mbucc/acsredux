package com.acsredux.auth.services;

import static com.acsredux.base.Util.dieIfBlank;

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

  void validateCommand(SignUpCommand x) {
    dieIfBlank(x.firstName(), "first name");
    dieIfBlank(x.lastName(), "last name");
    dieIfBlank(x.email(), "email");
    dieIfBlank(x.password(), "password");
  }

  Event handleSignUpCommand(SignUpCommand x) {
    validateCommand(x);
    System.out.println("handling SignUpCommand");
    return null;
  }
}
