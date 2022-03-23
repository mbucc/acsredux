package com.acsredux.core.members.ports;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.values.*;

public interface MemberWriter {
  MemberID addMember(CreateMember cmd, MemberStatus initialStatus, CreatedOn now);
  VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now);
  MemberID updateStatus(MemberID x1, MemberStatus x2);
  void writeSessionID(MemberID x1, SessionID x2);
  void setLastLogin(MemberID id, LoginTime now);
}
