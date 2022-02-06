package com.acsredux.lib.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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
    } catch (Exception e) {
      throw new RuntimeException(e);
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

  record MethodCall(Method method, Object[] args) {
    @Override
    public String toString() {
      return method.getName() + "(" + Arrays.toString(args) + ")";
    }
  }
}
