package com.acsredux.user.services;

import static com.acsredux.base.Util.dieIfBlank;
import static com.acsredux.base.Util.dieIfNull;

import com.acsredux.base.Command;
import com.acsredux.base.Event;
import com.acsredux.base.ValidationException;
import com.acsredux.base.entities.User;
import com.acsredux.base.values.CreatedOn;
import com.acsredux.base.values.Email;
import com.acsredux.base.values.UserID;
import com.acsredux.base.values.VerificationToken;
import com.acsredux.user.CommandHandler;
import com.acsredux.user.commands.AddUser;
import com.acsredux.user.events.UserAdded;
import com.acsredux.user.ports.Notifier;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.ports.Writer;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Optional;

public final class AddUserHandler {

  private final Reader reader;
  private final Writer writer;
  private final Notifier notifier;
  private final InstantSource clock;

  public AddUserHandler(
    Reader reader,
    Writer writer,
    Notifier notifier,
    InstantSource clock
  ) {
    this.reader = reader;
    this.writer = writer;
    this.notifier = notifier;
    this.clock = clock;
  }

  Accum validateAddUser(AddUser x) {
    dieIfNull(x.email(), "email");
    dieIfNull(x.password(), "password");
    checkEmailIsUnique(x.email());
    return new Accum(x, new CreatedOn(this.clock.instant()));
  }

  void checkEmailIsUnique(Email x) {
    if (reader.findByEmail(x).isPresent()) {
      throw new ValidationException("email already on file");
    }
  }

  Accum addUser(Accum x) {
    x.newUserID = writer.addUser(x.cmd, x.createdOn);
    return x;
  }

  Accum getToken(Accum x) {
    x.token = writer.addAddUserToken(x.newUserID, x.createdOn);
    return x;
  }

  UserAdded toUserAdded(Accum x) {
    return new UserAdded(x.cmd, x.createdOn, x.token);
  }

  UserAdded notify(UserAdded x) {
    notifier.userAdded(x);
    return x;
  }

  private static class Accum {

    AddUser cmd;
    UserID newUserID;
    VerificationToken token;
    CreatedOn createdOn;

    Accum(AddUser cmd, CreatedOn createdOn) {
      this.cmd = cmd;
      this.createdOn = createdOn;
    }
  }

  UserAdded handle(AddUser x) {
    return Optional
      .of(x)
      .map(this::validateAddUser)
      .map(this::addUser)
      .map(this::getToken)
      .map(this::toUserAdded)
      .map(this::notify)
      .get();
  }
}
