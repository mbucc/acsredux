package com.acsredux.members.ports;

import com.acsredux.members.entities.Member;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.Email;
import com.acsredux.members.values.MemberDashboard;
import java.util.Optional;

public interface Reader {
  Optional<Member> findByEmail(Email x);
  Optional<MemberDashboard> findMemberDashboard(FindMemberDashboard x);
}
