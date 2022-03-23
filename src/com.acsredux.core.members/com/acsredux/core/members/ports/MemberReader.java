package com.acsredux.core.members.ports;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import java.util.Optional;

public interface MemberReader {
  int countActiveMembers();
  Optional<Member> findByID(MemberID x);
  Optional<Member> findByEmail(Email x);
  Optional<Member> findByName(FirstName x1, LastName x2);
  Optional<Member> findBySessionID(SessionID x);
  MemberID getByToken(VerificationToken x);

  default Member getByID(MemberID x) {
    return findByID(x).orElseThrow(() -> new NotFoundException("member ID not found"));
  }

  default Member getByEmail(Email x) {
    return findByEmail(x).orElseThrow(() -> new NotFoundException("email not found"));
  }
}
