package com.acsredux.core.auth;

import com.acsredux.core.base.Subject;
import java.lang.reflect.Method;

public interface SecurityPolicy {
  boolean isAllowed(Method m, Object[] args);
  Subject getSubject(Object[] args);
}
