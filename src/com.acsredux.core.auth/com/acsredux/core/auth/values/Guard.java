package com.acsredux.core.auth.values;

import com.acsredux.core.base.Subject;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public record Guard(
  Predicate<Method> isMatchingResource,
  Predicate<Subject> isMatchingSubject
) {}
