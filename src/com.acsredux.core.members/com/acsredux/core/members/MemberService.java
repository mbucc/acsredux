package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import java.util.Optional;

public interface MemberService {
  public Event handle(MemberCommand x);

  public SessionID createSessionID(MemberID id);

  // queries
  Optional<MemberDashboard> findDashboard(MemberID x);
  MemberDashboard getDashboard(MemberID x);
  int activeMembers();
}
