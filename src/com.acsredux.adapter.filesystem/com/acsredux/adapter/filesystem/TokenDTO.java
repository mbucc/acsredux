package com.acsredux.adapter.filesystem;

import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.VerificationToken;
import java.util.Map;

public class TokenDTO {

  long memberID;
  String token;

  TokenDTO() {}

  public TokenDTO(Map.Entry<VerificationToken, MemberID> x) {
    this.token = x.getKey().val();
    this.memberID = x.getValue().val();
  }

  MemberID getMemberID() {
    return new MemberID(memberID);
  }

  VerificationToken getVerificationToken() {
    return new VerificationToken(token);
  }
}
