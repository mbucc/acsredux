package com.acsredux.core.base;

public class NotAuthorizedException extends RuntimeException {

  static final long serialVersionUID = 1L;

  public NotAuthorizedException(String message) {
    super(message);
  }

  public NotAuthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
}
