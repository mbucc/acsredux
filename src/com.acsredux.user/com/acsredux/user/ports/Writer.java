package com.acsredux.user.ports;

import com.acsredux.base.values.CreatedOn;
import com.acsredux.base.values.UserID;
import com.acsredux.base.values.VerificationToken;
import com.acsredux.user.commands.AddUser;
import java.time.Instant;

public interface Writer {
  UserID addUser(AddUser cmd, CreatedOn now);
  VerificationToken addAddUserToken(UserID userID, CreatedOn now);
}
