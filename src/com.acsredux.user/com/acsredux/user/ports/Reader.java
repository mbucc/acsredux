package com.acsredux.user.ports;

import com.acsredux.base.entities.User;
import com.acsredux.base.values.Email;
import com.acsredux.user.queries.FindUserDashboard;
import com.acsredux.user.values.UserDashboard;
import java.util.Optional;

public interface Reader {
  Optional<User> findByEmail(Email x);
  Optional<UserDashboard> findUserDashboard(FindUserDashboard x);
}
