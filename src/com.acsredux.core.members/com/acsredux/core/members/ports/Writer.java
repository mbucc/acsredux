package com.acsredux.core.members.ports;

import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.VerificationToken;

public interface Writer {
  MemberID addMember(AddMember cmd, CreatedOn now);
  VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now);
}
