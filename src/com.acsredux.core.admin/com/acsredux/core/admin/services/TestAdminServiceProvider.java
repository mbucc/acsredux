package com.acsredux.core.admin.services;

import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestAdminServiceProvider {

  @Test
  void testReaderCalled() {
    // setup
    var reader = new MockAdminReader(TEST_SITE_INFO);
    var handler = new AdminServiceProvider(reader);

    // execute
    var y = handler.getSiteInfo();

    // verify
    assertEquals(TEST_SITE_INFO, y);
  }
}
