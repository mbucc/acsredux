package com.acsredux.core.auth.validation.leaf.is.user;

import java.util.Map;
import validation.leaf.is.of.format.url.Code;
import validation.leaf.is.of.format.url.Message;
import validation.result.error.Error;

public class MustBeValidUser implements Error {

  @Override
  public Map<String, Object> value() {
    return Map.of("code", new Code().value(), "message", new Message().value());
  }
}
