package com.acsredux.user.services;

import static com.acsredux.base.Util.dieIfBlank;

import com.acsredux.user.QueryHandler;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.queries.FindUserDashboard;
import com.acsredux.user.values.UserDashboard;
import java.util.Optional;

public final class QueryHandlerImpl implements QueryHandler {

  private final Reader reader;

  public QueryHandlerImpl(Reader r) {
    this.reader = r;
  }

  @Override
  public Optional<UserDashboard> handle(FindUserDashboard x) {
    return reader.findUserDashboard(x);
  }
}
