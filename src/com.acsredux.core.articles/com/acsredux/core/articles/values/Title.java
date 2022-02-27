package com.acsredux.core.articles.values;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Title(List<Word> words) {
  public static Title of(String x) {
    return new Title(Arrays.stream(x.split(" ")).map(Word::new).toList());
  }

  @Override
  public String toString() {
    return words.stream().map(Word::toString).collect(Collectors.joining(" "));
  }
}
