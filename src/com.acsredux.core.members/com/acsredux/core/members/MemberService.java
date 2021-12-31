package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.queries.FindDashboard;
import com.acsredux.core.members.values.MemberDashboard;
import java.util.Optional;

public interface MemberService {
  public Event handle(MemberCommand x);

  // queries
  Optional<MemberDashboard> handle(FindDashboard x);
}
