package com.acsredux.members.services;

import com.acsredux.base.Event;
import com.acsredux.members.CommandService;
import com.acsredux.members.commands.AddMember;
import com.acsredux.members.commands.BaseCommand;
import com.acsredux.members.ports.Notifier;
import com.acsredux.members.ports.Reader;
import com.acsredux.members.ports.Writer;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.MemberDashboard;
import java.time.InstantSource;
import java.util.Optional;

public final class CommandProvider implements CommandService {

  private final AddMemberHandler addMemberHandler;
  private final Reader reader;

  public CommandProvider(Reader r, Writer w, Notifier notifier, InstantSource clock) {
    addMemberHandler = new AddMemberHandler(r, w, notifier, clock);
    this.reader = r;
  }

  @Override
  public Event handle(BaseCommand x) {
    if (x instanceof AddMember c) {
      return addMemberHandler.handle(c);
    } else {
      throw new IllegalArgumentException("invalid command " + x);
    }
  }

  @Override
  public Optional<MemberDashboard> handle(FindMemberDashboard x) {
    return reader.findMemberDashboard(x);
  }
}
