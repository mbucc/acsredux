package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_EMAIL;
import static com.acsredux.lib.testutil.TestData.TEST_FIRST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_LAST_NAME;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER;
import static com.acsredux.lib.testutil.TestData.TEST_MEMBER_ID;
import static com.acsredux.lib.testutil.TestData.TEST_SESSION_ID;
import static com.acsredux.lib.testutil.TestData.TEST_VERIFICATION_TOKEN;

import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import com.acsredux.core.members.values.VerificationToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockMemberReader implements MemberReader {

  List<String> calls = new ArrayList<>();

  @Override
  public Optional<Member> findByEmail(Email x) {
    if (x.equals(TEST_EMAIL)) {
      return Optional.of(TEST_MEMBER);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Member> findByName(FirstName x1, LastName x2) {
    if (x1.equals(TEST_FIRST_NAME) && x2.equals(TEST_LAST_NAME)) {
      return Optional.of(TEST_MEMBER);
    }
    return Optional.empty();
  }

  @Override
  public MemberID getByToken(VerificationToken x) {
    if (x.equals(TEST_VERIFICATION_TOKEN)) {
      return TEST_MEMBER_ID;
    }
    return null;
  }

  @Override
  public int countActiveMembers() {
    return 1;
  }

  @Override
  public Optional<Member> findByID(MemberID x) {
    if (x.equals(TEST_MEMBER_ID)) {
      return Optional.of(TEST_MEMBER);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Member> findBySessionID(SessionID x) {
    if (x.equals(TEST_SESSION_ID)) {
      return Optional.of(TEST_MEMBER);
    }
    return Optional.empty();
  }
}
