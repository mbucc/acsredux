package com.acsredux.core.members.ports;

import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import java.util.Optional;

public interface MemberReader {
  Optional<Member> findByEmail(Email x);

  default Member getByEmail(Email x) {
    return findByEmail(x).orElseThrow(() -> new NotFoundException("email not found"));
  }

  Optional<MemberDashboard> findMemberDashboard(MemberID x);

  default MemberDashboard getMemberDashboard(MemberID x) {
    return findMemberDashboard(x)
      .orElseThrow(() -> new NotFoundException("dashboard not found"));
  }
}
