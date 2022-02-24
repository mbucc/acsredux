package com.acsredux.core.auth;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.articles.ArticleService;
import com.acsredux.core.auth.values.Resource;
import com.acsredux.core.members.MemberService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.security.auth.Subject;

public class SecurityProxy implements InvocationHandler {

  private final Object handler;
  private final Resource resourceType;
  private final SecurityPolicy policy;

  private SecurityProxy(Object x1, SecurityPolicy x2, Resource x3) {
    this.handler = x1;
    this.policy = x2;
    this.resourceType = x3;
  }

  private static Object getProxy(Object x1, SecurityPolicy x2, Resource x3) {
    return Proxy.newProxyInstance(
      x1.getClass().getClassLoader(),
      x1.getClass().getInterfaces(),
      new SecurityProxy(x1, x2, x3)
    );
  }

  public static MemberService of(MemberService x1, SecurityPolicy x2) {
    return (MemberService) getProxy(x1, x2, Resource.MEMBERS);
  }

  public static AdminService of(AdminService x1, SecurityPolicy x2) {
    return (AdminService) getProxy(x1, x2, Resource.ADMIN_DATA);
  }

  public static ArticleService of(ArticleService x1, SecurityPolicy x2) {
    return (ArticleService) getProxy(x1, x2, Resource.ARTICLES);
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
      throw new RuntimeException(e);
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
