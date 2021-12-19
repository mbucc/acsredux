package com.acsredux.members.ports;

import com.acsredux.members.commands.AddMember;
import com.acsredux.members.values.CreatedOn;
import com.acsredux.members.values.MemberID;
import com.acsredux.members.values.VerificationToken;

public interface Writer {
  MemberID addMember(AddMember cmd, CreatedOn now);
  VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now);
}
