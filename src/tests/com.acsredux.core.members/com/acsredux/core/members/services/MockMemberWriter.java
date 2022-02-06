package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_VERIFICATION_TOKEN;

import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;

public class MockMemberWriter implements MemberWriter {

  @Override
  public MemberID addMember(AddMember cmd, MemberStatus initialStatus, CreatedOn now) {
    return TEST_MEMBER_ID;
  }

  @Override
  public VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now) {
    return TEST_VERIFICATION_TOKEN;
  }

  @Override
  public MemberID updateStatus(MemberID x1, MemberStatus x2) {
    return TEST_MEMBER_ID;
  }

  @Override
  public void writeSessionID(MemberID x1, SessionID x2) {}

  @Override
  public void setLastLogin(MemberID id, LoginTime now) {}
}
