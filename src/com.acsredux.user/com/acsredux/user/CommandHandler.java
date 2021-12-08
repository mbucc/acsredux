package com.acsredux.user;

import com.acsredux.base.Event;
import com.acsredux.user.commands.BaseCommand;

public interface CommandHandler {
  public Event handle(BaseCommand x);
}
