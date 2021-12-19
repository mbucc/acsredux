package com.acsredux.members;

import com.acsredux.base.Event;
import com.acsredux.members.commands.BaseCommand;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.MemberDashboard;
import java.util.Optional;

public interface CommandService {
  public Event handle(BaseCommand x);

  // queries
  Optional<MemberDashboard> handle(FindMemberDashboard x);
}
