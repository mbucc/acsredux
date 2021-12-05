package com.acsredux.auth.adapters;

import com.acsredux.auth.ports.Reader;
import com.acsredux.base.entities.User;
import com.acsredux.base.values.Email;
import java.util.Optional;

public class SQLiteReader implements Reader {

  @Override
  public Optional<User> findByEmail(Email x) {
    return null;
  }
}
