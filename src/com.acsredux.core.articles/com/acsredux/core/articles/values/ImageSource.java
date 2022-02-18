package com.acsredux.core.articles.values;

import java.net.MalformedURLException;
import java.net.URL;

public record ImageSource(URL url) {
  public static ImageSource of(String x) {
    try {
      return new ImageSource(new URL(x));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("invalid URL '" + x + "'");
    }
  }
}
