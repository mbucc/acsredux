package com.acsredux.core.auth.values;

import java.util.Objects;

public record Resource(String name) {
  public static Resource of(String x) {
    Objects.requireNonNull(x);
    // For now, just pass through.
    // TODO: Validate resource string.
    // Strings from the HOWTO-rbac.md document:
    //    ["member:*"]                         all members
    //    ["article:tag:{{email}}:edit"]       articles with a tag.
    //    ["member:{{email}}"]                 a specific member
    //    [ "member:*:email" ]                 all member's email field
    return new Resource(x);
  }
}
