package com.acsredux.core.base;

import java.time.Instant;
import java.util.UUID;
import javax.security.auth.Subject;

public interface Command {
  UUID guid();
  Instant created();
  Subject subject();
}
