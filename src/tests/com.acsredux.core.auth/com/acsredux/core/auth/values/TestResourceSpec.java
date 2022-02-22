package com.acsredux.core.auth.values;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TestResourceSpec {

  @ParameterizedTest
  @EnumSource
  void testResourceSetCorrectly(RESOURCE_TEST_DATA x) {
    ResourceSpec y = ResourceSpec.of(x.input);
    assertEquals(x.expected, y.resource());
  }

  private enum RESOURCE_TEST_DATA {
    ALL_MEMBERS("members:*", Resource.MEMBERS),
    MEMBER_ATTRIBUTE("members:*:email", Resource.MEMBERS),
    SPECIFIC_MEMBER("members:{{email}}", Resource.MEMBERS),
    ALL_ARTICLES("articles:*", Resource.ARTICLES),
    ARTICLES_MEMBER_EDIT_TAG("articles:tag:{{email}}:edit", Resource.ARTICLES),
    MEMBER_ARTICLES("articles:{{email}}", Resource.ARTICLES);

    final String input;
    final Resource expected;

    RESOURCE_TEST_DATA(String x1, Resource x2) {
      this.input = x1;
      this.expected = x2;
    }
  }
}
