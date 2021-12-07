package com.acsredux.user.events;

import com.acsredux.base.Event;
import com.acsredux.user.commands.AddUser;
import java.time.Instant;

public record UserAdded(AddUser command, Instant savedOn, String emailVerificationToken)
  implements Event {}
