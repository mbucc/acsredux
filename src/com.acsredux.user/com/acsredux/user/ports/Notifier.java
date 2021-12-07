package com.acsredux.user.ports;

import com.acsredux.user.events.UserAdded;

public interface Notifier {
  void userAdded(UserAdded event);
}
