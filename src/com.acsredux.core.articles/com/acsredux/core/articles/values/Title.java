package com.acsredux.core.articles.values;

import java.util.Arrays;
import java.util.List;

public record Title(List<Word> words) {
  public static Title of(String x) {
    return new Title(Arrays.stream(x.split(" ")).map(Word::new).toList());
  }
}
