package com.acsredux.user;

import com.acsredux.base.Event;
import com.acsredux.user.commands.UserCommand;

public interface CommandHandler {
  public Event handle(UserCommand x);
}
