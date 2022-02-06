package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.services.PasswordUtil;
import com.acsredux.core.members.values.*;
import java.util.Optional;

public interface MemberService {
  String ANONYMOUS_USERNAME = "Anonymous User";

  Event handle(MemberCommand x);

  SessionID createSessionID(MemberID id);

  // queries
  Optional<MemberDashboard> findDashboard(MemberID x);
  MemberDashboard getDashboard(MemberID x);
  int activeMembers();

  default String getAnonymousUsername() {
    return ANONYMOUS_USERNAME;
  }

  Optional<Member> findBySessionID(SessionID x);

  static void passwordSaltOrDie() {
    PasswordUtil.passwordSaltOrDie();
  }

  static HashedPassword hashpw(ClearTextPassword x) {
    return PasswordUtil.hashpw(x);
  }
}
