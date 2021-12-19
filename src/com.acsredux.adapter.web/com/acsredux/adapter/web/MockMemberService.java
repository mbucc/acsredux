package com.acsredux.adapter.web;

import com.acsredux.base.Event;
import com.acsredux.members.CommandService;
import com.acsredux.members.commands.BaseCommand;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.MemberDashboard;
import java.util.Optional;

class MockMemberService implements CommandService {

  private Event event;
  private MemberDashboard dashboard = null;

  @Override
  public Event handle(BaseCommand x) {
    return this.event;
  }

  @Override
  public Optional<MemberDashboard> handle(FindMemberDashboard x) {
    return Optional.ofNullable(this.dashboard);
  }

  void setEvent(Event x) {
    this.event = x;
  }

  void setDashboard(MemberDashboard x) {
    this.dashboard = x;
  }
}
