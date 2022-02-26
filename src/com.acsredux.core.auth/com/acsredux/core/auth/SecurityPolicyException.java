package com.acsredux.core.auth;

public class SecurityPolicyException extends RuntimeException {

  public SecurityPolicyException(String x) {
    super(x);
  }

  public SecurityPolicyException(Exception x) {
    super(x);
  }
}
