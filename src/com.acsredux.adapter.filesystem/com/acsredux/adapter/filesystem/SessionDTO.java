package com.acsredux.adapter.filesystem;

import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import java.util.Map;

public class SessionDTO {

  String session;
  long member;

  SessionDTO() {}

  SessionDTO(Map.Entry<SessionID, MemberID> x) {
    this.session = x.getKey().val();
    this.member = x.getValue().val();
  }

  SessionID getSessionID() {
    return new SessionID(session);
  }

  MemberID getMemberID() {
    return new MemberID(member);
  }
}
