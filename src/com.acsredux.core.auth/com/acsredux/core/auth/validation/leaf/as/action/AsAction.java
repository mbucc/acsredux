package com.acsredux.core.auth.validation.leaf.as.action;

import com.acsredux.core.auth.validation.leaf.is.action.IsAction;
import com.acsredux.core.auth.validation.leaf.is.action.MustBeValidAction;
import com.acsredux.core.auth.values.Action;
import validation.Validatable;
import validation.result.AbsentField;
import validation.result.FromNonSuccessful;
import validation.result.Result;
import validation.result.SuccessfulWithCustomValue;
import validation.result.error.Error;

public final class AsAction implements Validatable<Action> {

  private final Validatable<String> original;
  private final Error error;

  public AsAction(Validatable<String> original, Error error) throws Exception {
    if (original == null) {
      throw new Exception("Decorated validatable element can not be null");
    }
    if (error == null) {
      throw new Exception("Error can not be null");
    }

    this.original = original;
    this.error = error;
  }

  public AsAction(Validatable<String> original) throws Exception {
    this(original, new MustBeValidAction());
  }

  public Result<Action> result() throws Exception {
    Result<String> isActionResult = new IsAction(this.original, this.error).result();

    if (!isActionResult.isSuccessful()) {
      return new FromNonSuccessful<>(isActionResult);
    }

    if (!isActionResult.value().isPresent()) {
      return new AbsentField<>(isActionResult);
    }

    return new SuccessfulWithCustomValue<>(
      isActionResult,
      Action.valueOf(isActionResult.value().raw())
    );
  }
}
