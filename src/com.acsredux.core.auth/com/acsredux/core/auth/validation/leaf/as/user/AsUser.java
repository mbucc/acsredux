package com.acsredux.core.auth.validation.leaf.as.user;

import com.acsredux.core.auth.validation.leaf.is.user.IsUser;
import com.acsredux.core.auth.validation.leaf.is.user.MustBeValidUser;
import com.acsredux.core.auth.values.User;
import validation.Validatable;
import validation.result.AbsentField;
import validation.result.FromNonSuccessful;
import validation.result.Result;
import validation.result.SuccessfulWithCustomValue;
import validation.result.error.Error;

public final class AsUser implements Validatable<User> {

  private final Validatable<String> original;
  private final Error error;

  public AsUser(Validatable<String> original, Error error) throws Exception {
    if (original == null) {
      throw new Exception("Decorated validatable element can not be null");
    }
    if (error == null) {
      throw new Exception("Error can not be null");
    }

    this.original = original;
    this.error = error;
  }

  public AsUser(Validatable<String> original) throws Exception {
    this(original, new MustBeValidUser());
  }

  public Result<User> result() throws Exception {
    Result<String> isUserResult = new IsUser(this.original, this.error).result();

    if (!isUserResult.isSuccessful()) {
      return new FromNonSuccessful<>(isUserResult);
    }

    if (!isUserResult.value().isPresent()) {
      return new AbsentField<>(isUserResult);
    }

    return new SuccessfulWithCustomValue<>(
      isUserResult,
      new User(isUserResult.value().raw())
    );
  }
}
