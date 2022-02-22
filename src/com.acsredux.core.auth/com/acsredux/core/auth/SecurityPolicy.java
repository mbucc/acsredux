package com.acsredux.core.auth;

import com.acsredux.core.auth.values.Resource;
import java.lang.reflect.Method;

public interface SecurityPolicy {
  boolean isRecognizedMethod(Resource resourceType, Method m, Object[] args);
}
