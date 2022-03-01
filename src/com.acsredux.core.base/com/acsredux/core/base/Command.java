package com.acsredux.core.base;

import javax.security.auth.Subject;

public interface Command {
  Subject subject();
}
