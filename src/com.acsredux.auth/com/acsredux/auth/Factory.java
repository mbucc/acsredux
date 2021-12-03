package com.acsredux.auth;

import com.acsredux.auth.services.CommandHandlerImpl;

public final class Factory {

  public static CommandHandler getCommandHandler() {
    return new CommandHandlerImpl();
  }
}
