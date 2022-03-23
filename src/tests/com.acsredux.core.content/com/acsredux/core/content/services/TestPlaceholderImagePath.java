package com.acsredux.core.content.services;

import static com.acsredux.core.content.services.ContentServiceProvider.calculatePlaceholderImagePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acsredux.core.content.values.FileName;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class TestPlaceholderImagePath {

  @Test
  void sunny() {
    // setup
    var x = new FileName("./web/static/members/2/t.png");

    // execute
    var y = calculatePlaceholderImagePath(x);

    // verify
    var exp = Paths.get("./web/static/img/placeholder.png");
    assertEquals(exp, y);
  }
}
