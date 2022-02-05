package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import java.util.Optional;

public interface MemberService {
  public static final String ANONYMOUS_USERNAME = "Anonymous User";

  public Event handle(MemberCommand x);

  public SessionID createSessionID(MemberID id);

  // queries
  Optional<MemberDashboard> findDashboard(MemberID x);
  MemberDashboard getDashboard(MemberID x);
  int activeMembers();

  default String getAnonymousUsername() {
    return ANONYMOUS_USERNAME;
  }

  Optional<Member> findBySessionID(SessionID x);
}
