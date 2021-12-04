package com.acsredux.auth;

import com.acsredux.auth.services.CommandHandlerImpl;

public final class Factory {

  private Factory() {
    throw new UnsupportedOperationException("static only");
  }

  public static CommandHandler getCommandHandler() {
    return new CommandHandlerImpl();
  }
}
