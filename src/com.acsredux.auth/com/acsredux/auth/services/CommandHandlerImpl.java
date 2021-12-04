package com.acsredux.auth.services;

import static com.acsredux.base.Util.dieIfBlank;

import com.acsredux.auth.CommandHandler;
import com.acsredux.auth.commands.SignUpCommand;
import com.acsredux.auth.ports.Reader;
import com.acsredux.base.Command;
import com.acsredux.base.Event;
import com.acsredux.base.ValidationException;
import com.acsredux.base.entities.User;
import com.acsredux.base.values.Email;

public final class CommandHandlerImpl implements CommandHandler {

  private final Reader db;

  public CommandHandlerImpl(Reader db) {
    this.db = db;
  }

  @Override
  public Event handle(Command x) {
    if (x instanceof SignUpCommand c) {
      return handleSignUpCommand(c);
    } else {
      throw new IllegalArgumentException("invalid command " + x);
    }
  }

  void validateSignUpCommand(SignUpCommand x) {
    dieIfBlank(x.firstName(), "first name");
    dieIfBlank(x.lastName(), "last name");
    dieIfBlank(x.email(), "email");
    dieIfBlank(x.password(), "password");
  }

  void checkEmailIsUnique(Email x) {
    if (db.findByEmail(x).isPresent()) {
      throw new ValidationException("email already on file");
    }
  }

  Event handleSignUpCommand(SignUpCommand x) {
    validateSignUpCommand(x);
    System.out.println("handling SignUpCommand");
    return null;
  }
}
