package com.acsredux.core.auth.validation.leaf.as.resource;

import com.acsredux.core.auth.validation.leaf.is.resource.IsResource;
import com.acsredux.core.auth.validation.leaf.is.resource.MustBeValidResource;
import com.acsredux.core.auth.values.Resource;
import validation.Validatable;
import validation.result.*;
import validation.result.error.Error;

public final class AsResource implements Validatable<Resource> {

  private final Validatable<String> original;
  private final Error error;

  public AsResource(Validatable<String> original, Error error) throws Exception {
    if (original == null) {
      throw new Exception("Decorated validatable element can not be null");
    }
    if (error == null) {
      throw new Exception("Error can not be null");
    }

    this.original = original;
    this.error = error;
  }

  public AsResource(Validatable<String> original) throws Exception {
    this(original, new MustBeValidResource());
  }

  public Result<Resource> result() throws Exception {
    Result<String> isResourceResult = new IsResource(this.original, this.error).result();

    if (!isResourceResult.isSuccessful()) {
      return new FromNonSuccessful<>(isResourceResult);
    }

    if (!isResourceResult.value().isPresent()) {
      return new AbsentField<>(isResourceResult);
    }

    return new SuccessfulWithCustomValue<>(
      isResourceResult,
      new Resource(isResourceResult.value().raw())
    );
  }
}
