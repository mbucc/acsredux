package com.acsredux.auth;

import com.acsredux.auth.services.CommandHandlerImpl;
import com.acsredux.auth.ports.Reader;
import com.acsredux.auth.adapters.SQLiteReader;

public final class Factory {

  private Factory() {
    throw new UnsupportedOperationException("static only");
  }

  public static CommandHandler getCommandHandler() {
  return getCommandHandler(new SQLiteReader());
}

static CommandHandler getCommandHandler(Reader db) {
    return new CommandHandlerImpl(db);
  }
}
