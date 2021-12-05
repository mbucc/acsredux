package com.acsredux.user;

import com.acsredux.user.ports.Notifier;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.ports.Writer;
import com.acsredux.user.services.CommandHandlerImpl;
import java.time.InstantSource;

public final class Factory {

  private Factory() {
    throw new UnsupportedOperationException("static only");
  }

  public static CommandHandler getCommandHandler() {
    // TODO: Use java.util.ServiceLoader.
    return getCommandHandler(null, null, null, null);
  }

  static CommandHandler getCommandHandler(
    Reader r,
    Writer w,
    Notifier notifier,
    InstantSource clock
  ) {
    return new CommandHandlerImpl(r, w, notifier, clock);
  }
}
