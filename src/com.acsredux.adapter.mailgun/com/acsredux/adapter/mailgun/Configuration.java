package com.acsredux.adapter.mailgun;

import static com.acsredux.lib.env.Variable.*;
import static com.acsredux.lib.env.VariableUtil.*;

import java.net.URI;
import java.util.Map;

record Configuration(URI activationURL, int timeout, URI uri, char[] apiKey) {
  static Configuration loadFromEnvironment() {
    Map<String, String> env = System.getenv();
    return new Configuration(
      getURI(ACSREDUX_ACTIVATION_URL, env),
      getInt(NOTIFIER_SEND_TIMEOUT_IN_SECONDS, env),
      getURI(MAILGUN_API_URL, env),
      getSecret(MAILGUN_API_KEY, env)
    );
  }
}
