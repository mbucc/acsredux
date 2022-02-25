package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.BaseMemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import java.util.Optional;

public interface MemberService {
  String ANONYMOUS_USERNAME = "Anonymous User";

  // commands
  Event handle(BaseMemberCommand x);
  SessionID createSessionID(MemberID id);

  // queries
  Optional<MemberDashboard> findDashboard(MemberID x);
  MemberDashboard getDashboard(MemberID x);
  int activeMembers();

  Optional<Member> findBySessionID(SessionID x);

  static void passwordSaltOrDie() {
    PasswordUtil.passwordSaltOrDie();
  }

  static HashedPassword hashpw(ClearTextPassword x) {
    return PasswordUtil.hashpw(x);
  }
}
