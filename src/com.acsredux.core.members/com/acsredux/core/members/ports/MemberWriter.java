package com.acsredux.core.members.ports;

import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.values.*;

public interface MemberWriter {
  MemberID addMember(AddMember cmd, MemberStatus initialStatus, CreatedOn now);
  VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now);
}
