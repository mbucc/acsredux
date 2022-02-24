package com.acsredux.core.auth.values;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import javax.security.auth.Subject;

public record Guard(
  Predicate<Method> isMatchingResource,
  Predicate<Subject> isMatchingSubject
) {}
