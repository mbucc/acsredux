package com.acsredux.core.auth;

import com.acsredux.core.auth.values.Entitlement;
import com.acsredux.core.auth.values.Resource;
import com.acsredux.core.auth.values.SecurityPolicyDTO;
import com.spencerwi.either.Result;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*
Find command
  1. if another arg, goto 2.  else return null.
  2. check if it implements Command
  3. if so, return it
  4. go to 1.

find subject
  1. find command
  2. if null, return null
  3. cast, and extra subject.

Find action
  1. find command
  2. if null, return null.
  3. find "bottom-most" command class name (possible?)
  4. if command name starts with "create" or "add", return create.
  5. if command name starts with "update" return update
  6. if command name starts with "delete" return delete
     HMMM .... this is weak.
     I'd rather register every command and use instanceof.
     then return false (and email an error) if we hit an
     unknown command.

What about queries?

  One possibility: use ArchUnit to ensure all service methods either:
    1. take a command as an argument, or
    2. return a value that implements the marker interface QueryResponse.





 */
public class SecurityPolicyProvider implements SecurityPolicy {

  private final List<Entitlement> entitlements;
  private final Map<Method, Predicate<Object[]>> protectedMethods;

  public SecurityPolicyProvider(List<Entitlement> entitlements) {
    this.entitlements = entitlements;
    protectedMethods = new HashMap<>();
  }

  public List<Entitlement> entitlements() {
    return entitlements;
  }

  public boolean isAllowed(Resource x1, Method m, Object[] args) {
    Predicate<Object[]> check = protectedMethods.get(m);
    if (check == null) {
      return true;
    }
    return check.test(args);
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

  @Override
  public boolean isRecognizedMethod(Resource resourceType, Method m, Object[] args) {
    return false;
  }
}
