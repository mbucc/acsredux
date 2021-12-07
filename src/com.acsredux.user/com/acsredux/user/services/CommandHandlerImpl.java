package com.acsredux.user.services;

import static com.acsredux.base.Util.dieIfBlank;

import com.acsredux.base.Event;
import com.acsredux.user.CommandHandler;
import com.acsredux.user.commands.AddUser;
import com.acsredux.user.commands.UserCommand;
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
  public Event handle(UserCommand x) {
    return switch (x) {
      case AddUser c -> addUserHandler.handle(c);
    };
  }
}
