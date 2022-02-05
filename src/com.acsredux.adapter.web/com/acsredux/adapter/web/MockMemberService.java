package com.acsredux.adapter.web;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import java.util.Optional;

public class MockMemberService implements MemberService {

  private Event event;
  private Member member;
  private MemberDashboard dashboard = null;
  private String anonymousUsername = "Test Anonymous User";

  @Override
  public Event handle(MemberCommand x) {
    return this.event;
  }

  @Override
  public Optional<MemberDashboard> findDashboard(MemberID x) {
    return Optional.ofNullable(this.dashboard);
  }

  @Override
  public MemberDashboard getDashboard(MemberID x) {
    return this.dashboard;
  }

  @Override
  public int activeMembers() {
    return 1;
  }

  @Override
  public SessionID createSessionID(MemberID x) {
    return new SessionID("aSessionIDForMember" + x.val());
  }

  @Override
  public String getAnonymousUsername() {
    return anonymousUsername;
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

  void setEvent(Event x) {
    this.event = x;
  }

  void setDashboard(MemberDashboard x) {
    this.dashboard = x;
  }
}
