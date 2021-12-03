package com.acsredux.base;

public class ValidationError extends Exception {

  String message;

  public ValidationError(String message) {
    message = message;
  }

  public ValidationError(String message, Throwable cause) {
    super(message, cause);
  }
}
