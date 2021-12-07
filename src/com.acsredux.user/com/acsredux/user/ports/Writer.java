package com.acsredux.user.ports;

import com.acsredux.user.commands.AddUser;
import java.time.Instant;

public interface Writer {
  long addUser(AddUser cmd, Instant now);
  String addAddUserToken(long userID, Instant now);
}
