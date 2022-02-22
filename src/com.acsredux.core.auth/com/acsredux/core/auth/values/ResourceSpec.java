package com.acsredux.core.auth.values;

import java.util.Objects;

public record ResourceSpec(String name, Resource resource) {
  public static ResourceSpec of(String x) {
    Objects.requireNonNull(x);
    return new ResourceSpec(x, specToResource(x));
  }

  private static Resource specToResource(String x) {
    return Resource.valueOf(x.split(":")[0].toUpperCase());
  }
}
