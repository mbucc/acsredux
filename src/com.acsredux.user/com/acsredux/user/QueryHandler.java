package com.acsredux.user;

import com.acsredux.user.queries.FindUserDashboard;
import com.acsredux.user.values.UserDashboard;
import java.util.Optional;

public interface QueryHandler {
  Optional<UserDashboard> handle(FindUserDashboard x);
}
