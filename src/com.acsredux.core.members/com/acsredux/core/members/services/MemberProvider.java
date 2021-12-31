package com.acsredux.core.members.services;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.ports.AdminReader;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.ports.Reader;
import com.acsredux.core.members.ports.Writer;
import com.acsredux.core.members.queries.FindDashboard;
import com.acsredux.core.members.values.MemberDashboard;
import java.time.InstantSource;
import java.util.Optional;

public final class MemberProvider implements MemberService {

  private final AddMemberHandler addMemberHandler;
  private final Reader reader;
  private final AdminReader adminReader;

  public MemberProvider(
    Reader r,
    Writer w,
    Notifier notifier,
    InstantSource clock,
    AdminReader adminReader
  ) {
    addMemberHandler = new AddMemberHandler(r, adminReader, w, notifier, clock);
    this.reader = r;
    this.adminReader = adminReader;
  }

  @Override
  public Event handle(MemberCommand x) {
    if (x instanceof AddMember c) {
      return addMemberHandler.handle(c);
    } else {
      throw new IllegalArgumentException("invalid command " + x);
    }
  }

  @Override
  public Optional<MemberDashboard> handle(FindDashboard x) {
    return reader.findMemberDashboard(x);
  }
}
