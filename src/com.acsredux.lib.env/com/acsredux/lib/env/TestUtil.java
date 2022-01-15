package com.acsredux.lib.env;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TestUtil {

  @Test
  void testGetURISunny() throws URISyntaxException {
    // setup
    Map<String, String> env = Map.of(
      Variable.MAILGUN_API_URL.name(),
      "https://example.com"
    );

    // execute
    URI y = assertDoesNotThrow(() -> Util.getURI(Variable.MAILGUN_API_URL, env));

    // verify
    assertEquals(new URI("https://example.com"), y);
  }

  @Test
  void testGetURINull() throws URISyntaxException {
    // setup
    Map<String, String> env = Collections.emptyMap();

    // execute
    Exception y = assertThrows(
      IllegalStateException.class,
      () -> Util.getURI(Variable.ACSREDUX_ACTIVATION_URL, env)
    );

    // verify
    assertEquals(
      "Error loading environmental variable ACSREDUX_ACTIVATION_URL (null).",
      y.getMessage()
    );
  }

  @Test
  void getGetIntSunny() {
    // setup
    Map<String, String> env = Map.of(Variable.MAILGUN_API_URL.name(), "1");

    // execute
    int y = assertDoesNotThrow(() -> Util.getInt(Variable.MAILGUN_API_URL, env));

    // verify
    assertEquals(1, y);
  }

  @Test
  void testGetIntNull() {
    // setup
    Map<String, String> env = Collections.emptyMap();

    // execute
    int y = assertDoesNotThrow(() ->
      Util.getInt(Variable.NOTIFIER_SEND_TIMEOUT_IN_SECONDS, env)
    );

    // verify
    Integer exp = Integer.valueOf(
      Variable.NOTIFIER_SEND_TIMEOUT_IN_SECONDS.getDefaultValue()
    );
    assertEquals(exp, y);
  }
  //  void testFormToAddMemberCommand() {
  //    // setup
  //    FormData x = new FormData();
  //    x.add("email", TEST_EMAIL);
  //    x.add("firstName", TEST_FIRST_NAME);
  //    x.add("lastName", TEST_LAST_NAME);
  //    x.add("zip", TEST_ZIP_CODE);
  //    x.add("pwd1", TEST_CLEARTEXT_PASSWORD);
  //    x.add("pwd2", TEST_CLEARTEXT_PASSWORD);
  //    x.add("command", CREATE_MEMBER);
  //
  //    // execute
}
