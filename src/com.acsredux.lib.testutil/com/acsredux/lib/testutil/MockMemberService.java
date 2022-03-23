package com.acsredux.lib.testutil;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.BaseMemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.SessionID;
import java.util.Optional;

public class MockMemberService implements MemberService {

  private Event event;
  private Member member;
  private String anonymousUsername = "Test Anonymous User";

  @Override
  public Event handle(BaseMemberCommand x) {
    return this.event;
  }

  @Override
  public int activeMembers() {
    return 1;
  }

  @Override
  public Member getByID(MemberID x) {
    if (this.member == null) {
      throw new NotFoundException("no member with id " + x);
    }
    return this.member;
  }

  @Override
  public SessionID createSessionID(MemberID x) {
    return new SessionID("aSessionIDForMember" + x.val());
  }

  @Override
  public Optional<Member> findBySessionID(SessionID x) {
    return Optional.ofNullable(this.member);
  }

  public void setAnonymousUsername(String x) {
    anonymousUsername = x;
  }

  public void setMember(Member x) {
    this.member = x;
  }

  public void setEvent(Event x) {
    this.event = x;
  }
}
