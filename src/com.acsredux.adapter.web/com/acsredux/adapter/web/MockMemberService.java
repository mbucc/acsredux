package com.acsredux.adapter.web;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.queries.FindDashboard;
import com.acsredux.core.members.values.MemberDashboard;
import java.util.Optional;

class MockMemberService implements MemberService {

  private Event event;
  private MemberDashboard dashboard = null;

  @Override
  public Event handle(MemberCommand x) {
    return this.event;
  }

  @Override
  public Optional<MemberDashboard> handle(FindDashboard x) {
    return Optional.ofNullable(this.dashboard);
  }

  void setEvent(Event x) {
    this.event = x;
  }

  void setDashboard(MemberDashboard x) {
    this.dashboard = x;
  }
}
