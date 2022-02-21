package com.acsredux.core.auth;

import com.acsredux.core.auth.values.*;
import com.spencerwi.either.Result;
import java.util.List;
import java.util.stream.Collectors;

public record SecurityPolicy(List<Entitlement> entitlements) {
  public static Result<SecurityPolicy> parse(String s) {
    return Result.attempt(() -> SecurityPolicyDTO.parse(s)).map(SecurityPolicy::of);
  }

  static SecurityPolicy of(SecurityPolicyDTO x) {
    return new SecurityPolicy(
      x.acls
        .stream()
        .map(SecurityPolicyDTO.ACL::asEntitlement)
        .collect(Collectors.toList())
    );
  }
}
