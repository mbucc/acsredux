package com.acsredux.lib.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.base.ValidationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 Proxy that keeps track of which methods have been called.
 */
public class MockProxy implements InvocationHandler {

  record MethodCall(Method method, Object[] args) {
    @Override
    public String toString() {
      return method.getName() + "(" + Arrays.toString(args) + ")";
    }
  }

  private final Object obj;

  private final List<MethodCall> calls = new ArrayList<>();

  private MockProxy(Object obj) {
    this.obj = obj;
  }

  public static Object of(Object obj) {
    return Proxy.newProxyInstance(
      obj.getClass().getClassLoader(),
      obj.getClass().getInterfaces(),
      new MockProxy(obj)
    );
  }

  public static MockProxy toProxy(Object x) {
    if (!Proxy.isProxyClass(x.getClass())) {
      throw new IllegalStateException(x + " is not a proxy");
    }
    return (MockProxy) Proxy.getInvocationHandler(x);
  }

  @Override
  public Object invoke(Object proxy, Method m, Object[] args) {
    this.calls.add(new MethodCall(m, args));
    try {
      return m.invoke(obj, args);
    } catch (NotFoundException | AuthenticationException | ValidationException e) {
      throw e;
    } catch (Exception e) {
      Throwable cause = e.getCause();
      if (cause == null) {
        throw new RuntimeException(e);
      }
      switch (cause) {
        case NotFoundException e2 -> throw e2;
        case AuthenticationException e2 -> throw e2;
        case ValidationException e2 -> throw e2;
        default -> throw new RuntimeException(e);
      }
    }
  }

  public MockProxy assertCallCount(int expectedCallCount) {
    assertEquals(calls.toString(), expectedCallCount, this.calls.size());
    return this;
  }

  public MockProxy assertCall(int i, String expectedMethodName, Object... expectedArgs) {
    MethodCall y = calls.get(i);
    String prefix = "call " + i + ": ";
    assertEquals(prefix, expectedMethodName, y.method.getName());
    assertArrayEquals(
      expectedArgs,
      Objects.requireNonNullElse(y.args, new Object[] {}),
      prefix
    );
    return this;
  }
}
