package com.acsredux.core.auth;

import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.auth.values.Entitlement;
import com.acsredux.core.auth.values.Guard;
import com.acsredux.core.auth.values.SecurityPolicyDTO;
import com.acsredux.core.base.Command;
import com.acsredux.core.members.values.MemberPrincipal;
import com.spencerwi.either.Result;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.security.auth.Subject;

public class SecurityPolicyProvider implements SecurityPolicy {

  private final List<Entitlement> entitlements;
  private final List<Guard> guards;

  public SecurityPolicyProvider(List<Entitlement> entitlements) {
    this.entitlements = entitlements;
    this.guards =
      entitlements
        .stream()
        .map(SecurityPolicyProvider::toGuard)
        .collect(Collectors.toList());
  }

  private static Guard toGuard(Entitlement entitlement) {
    return new Guard(
      SecurityPolicyProvider::isCreateArticle,
      SecurityPolicyProvider::isLoggedIn
    );
  }

  static boolean isLoggedIn(Subject x) {
    return !x.getPrincipals(MemberPrincipal.class).isEmpty();
  }

  static boolean isCreateArticle(Method x) {
    return (
      x.getParameterCount() > 0 &&
      CreateArticleCommand.class.isAssignableFrom(x.getParameterTypes()[0])
    );
  }

  public List<Entitlement> entitlements() {
    return new ArrayList<>(entitlements);
  }

  public boolean isAllowed(Method m, Object[] args) {
    boolean isNotGuarded = true;
    boolean isPastGuard = false;
    for (Guard x : guards) {
      if (x.isMatchingResource().test(m)) {
        isNotGuarded = false;
        isPastGuard = x.isMatchingSubject().test(getSubject(args));
      }
    }
    return isNotGuarded || isPastGuard;
  }

  @Override
  public Subject getSubject(Object[] args) {
    if (args.length > 0) {
      return ((Command) args[0]).subject();
    }
    throw new IllegalStateException("no subject found.");
  }

  public static Result<SecurityPolicyProvider> parse(String s) {
    return Result
      .attempt(() -> SecurityPolicyDTO.parse(s))
      .map(SecurityPolicyProvider::of);
  }

  static SecurityPolicyProvider of(SecurityPolicyDTO x) {
    return new SecurityPolicyProvider(
      x.acls
        .stream()
        .map(SecurityPolicyDTO.ACL::asEntitlement)
        .collect(Collectors.toList())
    );
  }
}