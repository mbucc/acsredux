package com.acsredux.adapter.sendgrid;

import static com.acsredux.lib.env.Util.*;
import static com.acsredux.lib.env.Variable.*;

import java.net.URI;
import java.util.Map;

record Configuration(
  URI activationURL,
  int timeout,
  String welcomeTemplateId,
  URI apiURI,
  char[] apiKey
) {
  static Configuration loadFromEnvironment() {
    Map<String, String> env = System.getenv();
    return new Configuration(
      getURI(ACSREDUX_ACTIVATION_URL, env),
      getInt(NOTIFIER_SEND_TIMEOUT_IN_SECONDS, env),
      getString(SENDGRID_WELCOME_TEMPLATE_ID, env),
      getURI(SENDGRID_API_URL, env),
      getSecret(SENDGRID_API_KEY, env)
    );
  }
}
