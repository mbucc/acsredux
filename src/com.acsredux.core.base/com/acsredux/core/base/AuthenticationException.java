package com.acsredux.core.base;

public class AuthenticationException extends RuntimeException {

  static final long serialVersionUID = 1L;

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
