package com.acsredux.core.content.values;

import java.util.Arrays;
import java.util.List;

public record AltText(List<Word> text) {
  public static AltText of(String s) {
    return new AltText(Arrays.stream(s.split(" ")).map(Word::of).toList());
  }
}
