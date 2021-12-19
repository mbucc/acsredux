package com.acsredux.members.ports;

import com.acsredux.members.events.MemberAdded;

public interface Notifier {
  void memberAdded(MemberAdded event);
}
