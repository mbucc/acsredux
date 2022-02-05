package com.acsredux.core.members.ports;

import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import java.util.Optional;

public interface MemberReader {
  Optional<Member> findByEmail(Email x);

  Optional<Member> findByName(FirstName x1, LastName x2);

  default Member getByEmail(Email x) {
    return findByEmail(x).orElseThrow(() -> new NotFoundException("email not found"));
  }

  Optional<MemberDashboard> findMemberDashboard(MemberID x);

  default MemberDashboard getMemberDashboard(MemberID x) {
    return findMemberDashboard(x)
      .orElseThrow(() -> new NotFoundException("dashboard not found"));
  }

  // Queries
  MemberID getByToken(VerificationToken x);
  Member getByID(MemberID x);
  int countActiveMembers();
  Optional<Member> findBySessionID(SessionID x);
}
