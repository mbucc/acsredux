package com.acsredux.core.auth;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.articles.ArticleService;
import com.acsredux.core.base.AuthenticationException;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.MemberService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.security.auth.Subject;

public class SecurityProxy implements InvocationHandler {

  private final Object handler;
  private final SecurityPolicy policy;

  private SecurityProxy(Object x1, SecurityPolicy x2) {
    this.handler = x1;
    this.policy = x2;
  }

  private static Object getProxy(Object x1, SecurityPolicy x2) {
    return Proxy.newProxyInstance(
      x1.getClass().getClassLoader(),
      x1.getClass().getInterfaces(),
      new SecurityProxy(x1, x2)
    );
  }

  public static MemberService of(MemberService x1, SecurityPolicy x2) {
    return (MemberService) getProxy(x1, x2);
  }

  public static AdminService of(AdminService x1, SecurityPolicy x2) {
    return (AdminService) getProxy(x1, x2);
  }

  public static ArticleService of(ArticleService x1, SecurityPolicy x2) {
    return (ArticleService) getProxy(x1, x2);
  }

  @Override
  public Object invoke(Object proxy, Method m, Object[] args) {
    try {
      if (policy.isAllowed(m, args)) {
        return m.invoke(handler, args);
      } else {
        String fmt = "%s denied access to %s";
        throw new SecurityPolicyException(
          String.format(fmt, dump(policy.getSubject(args)), dump(m, args))
        );
      }
    } catch (SecurityPolicyException e) {
      throw e;
    } catch (Exception e) {
      if (e.getCause() == null) {
        throw new RuntimeException(e);
      }
      switch (e.getCause()) {
        case NotFoundException e2 -> throw e2;
        case AuthenticationException e2 -> throw e2;
        case ValidationException e2 -> throw e2;
        default -> throw new RuntimeException(e);
      }
    }
  }

  // Compress onto one line.
  static String dump(Subject x) {
    if (x == null) {
      return "null";
    }
    return x
      .toString()
      .replaceAll("[\\r\\n]", " ")
      .replaceAll("\\t+", " ")
      .replaceAll("  *", " ")
      .replaceAll(" *$", "");
  }

  static String dump(Method m, Object[] args) {
    StringBuilder sb = new StringBuilder();
    sb.append(m.getDeclaringClass().getName());
    sb.append(".");
    sb.append(m.getName());
    sb.append("(");
    for (int i = 0; i < args.length; i++) {
      Object a = args[i];
      if (a == null) {
        sb.append("null");
      } else {
        sb.append(a.getClass().getSimpleName());
      }
      sb.append(" x");
      sb.append(i + 1);
      if (i < args.length - 1) {
        sb.append(", ");
      } else {
        sb.append(")");
      }
    }
    return sb.toString();
  }
}
