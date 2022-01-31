package com.acsredux.adapter.web;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import java.util.Optional;

class MockMemberService implements MemberService {

  private Event event;
  private MemberDashboard dashboard = null;

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

  void setEvent(Event x) {
    this.event = x;
  }

  void setDashboard(MemberDashboard x) {
    this.dashboard = x;
  }
}
