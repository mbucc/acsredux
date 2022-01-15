package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import java.util.Optional;

public interface MemberService {
  public Event handle(MemberCommand x);

  // queries
  Optional<MemberDashboard> findDashboard(MemberID x);
  MemberDashboard getDashboard(MemberID x);
}
