package com.acsredux.user.ports;

import com.acsredux.base.entities.User;
import java.util.Optional;

public interface Reader {
  Optional<User> findByEmail(String x);
}
