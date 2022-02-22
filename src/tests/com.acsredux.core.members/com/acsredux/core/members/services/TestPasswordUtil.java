package com.acsredux.core.members.services;

import static com.acsredux.core.members.PasswordUtil.checkpw;
import static com.acsredux.core.members.PasswordUtil.hashpw;
import static com.acsredux.lib.testutil.TestData.TEST_CLEAR_TEXT_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.HashedPassword;
import org.junit.jupiter.api.Test;

class TestPasswordUtil {

  final HashedPassword hashed = hashpw(TEST_CLEAR_TEXT_PASSWORD);

  @Test
  void testRoundTrip() {
    assertTrue(checkpw(TEST_CLEAR_TEXT_PASSWORD, hashed));
  }

  @Test
  void testNoMatch() {
    assertFalse(checkpw(ClearTextPassword.of("123xX!"), hashed));
  }
}
