package com.acsredux.core.articles.values;

import java.net.URL;

public record Image(ImageSource source, AltText altText, URL href)
  implements SectionElement {
  public static Image of(String x) {
    return new Image(ImageSource.of(x), null, null);
  }
}
