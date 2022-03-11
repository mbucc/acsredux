package com.acsredux.core.content.values;

public record Image(ImageSource source, AltText altText) implements SectionElement {
  public static Image of(String x) {
    return new Image(new ImageSource(x), null);
  }

  public static Image of(ImageSource x) {
    return new Image(x, null);
  }
}
