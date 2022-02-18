package com.acsredux.core.base;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

public interface Command {
  UUID guid();
  Instant created();
  Principal principal();
}
