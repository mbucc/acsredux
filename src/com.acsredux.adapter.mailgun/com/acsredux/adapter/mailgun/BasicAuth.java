package com.acsredux.adapter.mailgun;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

class BasicAuth extends Authenticator {

  private final Configuration conf;

  public BasicAuth(Configuration conf) {
    this.conf = conf;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication("api", conf.apiKey());
  }
}
