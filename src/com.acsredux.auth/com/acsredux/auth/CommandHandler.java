package com.acsredux.auth;

import com.acsredux.base.Command;
import com.acsredux.base.Event;

public interface CommandHandler {
  public Event handle(Command x);
}
