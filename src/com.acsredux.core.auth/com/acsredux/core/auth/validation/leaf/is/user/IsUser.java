package com.acsredux.core.auth.validation.leaf.is.user;

import com.acsredux.core.auth.values.User;
import validation.Validatable;
import validation.result.AbsentField;
import validation.result.NonSuccessfulWithCustomError;
import validation.result.Result;
import validation.result.error.Error;

public final class IsUser implements Validatable<String> {

  private final Validatable<String> original;
  private final Error error;

  public IsUser(Validatable<String> original, Error error) throws Exception {
    if (original == null) {
      throw new Exception("Decorated validatable element can not be null");
    }
    if (error == null) {
      throw new Exception("Error can not be null");
    }

    this.original = original;
    this.error = error;
  }

  public IsUser(Validatable<String> original) throws Exception {
    this(original, new MustBeValidUser());
  }

  public Result<String> result() throws Exception {
    Result<String> prevResult = this.original.result();

    if (!prevResult.isSuccessful()) {
      return prevResult;
    }

    if (!prevResult.value().isPresent()) {
      return new AbsentField<>(prevResult);
    }

    if (!this.isValidUser(prevResult)) {
      return new NonSuccessfulWithCustomError<>(prevResult, this.error);
    }

    return prevResult;
  }

  private Boolean isValidUser(Result<String> result) throws Exception {
    try {
      User.of(result.value().raw());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
