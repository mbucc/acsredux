package com.acsredux.user.events;

import com.acsredux.base.Event;
import com.acsredux.base.values.CreatedOn;
import com.acsredux.base.values.VerificationToken;
import com.acsredux.user.commands.AddUser;
import java.time.Instant;

public record UserAdded(AddUser cmd, CreatedOn on, VerificationToken tok)
  implements Event {}
