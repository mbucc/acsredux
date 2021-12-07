package com.acsredux.user.ports;

import com.acsredux.base.entities.User;
import com.acsredux.base.values.Email;
import java.util.Optional;

public interface Reader {
  Optional<User> findByEmail(Email x);
}
