package com.acsredux.core.auth.validation.leaf.is.resource;

import com.acsredux.core.auth.values.Resource;
import validation.Validatable;
import validation.result.AbsentField;
import validation.result.NonSuccessfulWithCustomError;
import validation.result.Result;
import validation.result.error.Error;

public final class IsResource implements Validatable<String> {

  private Validatable<String> original;
  private Error error;

  public IsResource(Validatable<String> original, Error error) throws Exception {
    if (original == null) {
      throw new Exception("Decorated validatable element can not be null");
    }
    if (error == null) {
      throw new Exception("Error can not be null");
    }

    this.original = original;
    this.error = error;
  }

  public IsResource(Validatable<String> original) throws Exception {
    this(original, new MustBeValidResource());
  }

  public Result<String> result() throws Exception {
    Result<String> prevResult = this.original.result();

    if (!prevResult.isSuccessful()) {
      return prevResult;
    }

    if (!prevResult.value().isPresent()) {
      return new AbsentField<>(prevResult);
    }

    if (!this.isValidResource(prevResult)) {
      return new NonSuccessfulWithCustomError<>(prevResult, this.error);
    }

    return prevResult;
  }

  private Boolean isValidResource(Result<String> result) throws Exception {
    try {
      Resource.of(result.value().raw());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
