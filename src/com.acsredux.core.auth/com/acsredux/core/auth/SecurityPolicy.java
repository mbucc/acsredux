package com.acsredux.core.auth;

import java.lang.reflect.Method;
import javax.security.auth.Subject;

public interface SecurityPolicy {
  boolean isAllowed(Method m, Object[] args);
  Subject getSubject(Object[] args);
}
