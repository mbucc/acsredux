package com.acsredux.core.base;

public class NotFoundException extends RuntimeException {

  static final long serialVersionUID = 1L;

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
