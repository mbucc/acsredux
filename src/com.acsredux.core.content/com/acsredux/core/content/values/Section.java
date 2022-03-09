package com.acsredux.core.content.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: rename content attribute to elements.
public record Section(Title title, List<SectionElement> content) {
  public Section with(Image x1) {
    List<SectionElement> ys = new ArrayList<>(content);
    ys.add(x1);
    return new Section(title, Collections.unmodifiableList(ys));
  }
}
