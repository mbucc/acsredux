package com.acsredux.members;

import com.acsredux.members.ports.Notifier;
import com.acsredux.members.ports.Reader;
import com.acsredux.members.ports.Writer;
import com.acsredux.members.services.CommandProvider;
import java.time.Clock;
import java.time.InstantSource;
import java.time.ZoneId;

public final class ServiceFactory {

  private ServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static CommandService getCommandService(
    Reader r,
    Writer w,
    Notifier notifier,
    ZoneId tz
  ) {
    return getCommandService(r, w, notifier, Clock.system(tz));
  }

  static CommandService getCommandService(
    Reader r,
    Writer w,
    Notifier notifier,
    InstantSource clock
  ) {
    return new CommandProvider(r, w, notifier, clock);
  }
}
