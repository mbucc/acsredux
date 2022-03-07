package com.acsredux.adapter.web.common;

import java.nio.charset.StandardCharsets;

public record FormFieldPart(String name, byte[] val) implements MultiPart {
  public String valAsString() {
    return new String(val(), StandardCharsets.ISO_8859_1);
  }
}
