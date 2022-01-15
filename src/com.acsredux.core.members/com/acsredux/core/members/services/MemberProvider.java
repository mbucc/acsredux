package com.acsredux.core.members.services;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.ports.AdminReader;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.ports.Writer;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import java.time.InstantSource;
import java.util.Optional;

public final class MemberProvider implements MemberService {

  private final AddMemberHandler addMemberHandler;
  private final MemberReader reader;
  private final AdminReader adminMemberReader;

  public MemberProvider(
    MemberReader r,
    Writer w,
    Notifier notifier,
    InstantSource clock,
    AdminReader adminMemberReader
  ) {
    addMemberHandler = new AddMemberHandler(r, adminMemberReader, w, notifier, clock);
    this.reader = r;
    this.adminMemberReader = adminMemberReader;
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
  public Optional<MemberDashboard> findDashboard(MemberID x) {
    return reader.findMemberDashboard(x);
  }

  @Override
  public MemberDashboard getDashboard(MemberID x) {
    return reader
      .findMemberDashboard(x)
      .orElseThrow(() ->
        new NotFoundException("Member ID " + x.val() + " is not on file.")
      );
  }
}
