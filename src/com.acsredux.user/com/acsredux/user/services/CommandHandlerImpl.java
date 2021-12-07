package com.acsredux.user.services;

import static com.acsredux.base.Util.dieIfBlank;

import com.acsredux.base.Command;
import com.acsredux.base.Event;
import com.acsredux.user.CommandHandler;
import com.acsredux.user.commands.AddUser;
import com.acsredux.user.ports.Notifier;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.ports.Writer;
import java.time.InstantSource;

public final class CommandHandlerImpl implements CommandHandler {

  private final AddUserHandler addUserHandler;

  public CommandHandlerImpl(Reader r, Writer w, Notifier notifier, InstantSource clock) {
    addUserHandler = new AddUserHandler(r, w, notifier, clock);
  }

  @Override
  public Event handle(Command x) {
    if (x instanceof AddUser c) {
      return addUserHandler.handle(c);
    } else {
      throw new IllegalArgumentException("invalid command " + x);
    }
  }
}
