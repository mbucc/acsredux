package com.acsredux.core.members;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.BaseMemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.entities.PublicMember;
import com.acsredux.core.members.values.*;
import java.util.Optional;

public interface MemberService {
  // commands
  Event handle(BaseMemberCommand x);
  SessionID createSessionID(MemberID id);

  // queries
  int activeMembers();
  Member getByID(MemberID x);
  Optional<Member> findBySessionID(SessionID x);

  default PublicMember getPublicByID(MemberID x) {
    return getByID(x).asPublic();
  }

  // other
  String ANONYMOUS_USERNAME = "Anonymous User";

  static void passwordSaltOrDie() {
    PasswordUtil.passwordSaltOrDie();
  }

  static HashedPassword hashpw(ClearTextPassword x) {
    return PasswordUtil.hashpw(x);
  }
}
