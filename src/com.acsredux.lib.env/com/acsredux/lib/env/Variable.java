package com.acsredux.lib.env;

import static com.acsredux.lib.env.VariableType.INT;
import static com.acsredux.lib.env.VariableType.SECRET;
import static com.acsredux.lib.env.VariableType.STRING;
import static com.acsredux.lib.env.VariableType.URL;

public enum Variable {
  ACSREDUX_ACTIVATION_URL(URL),
  ACSREDUX_SECRET_FILENAME(STRING, "~/.acsredux"),
  SENDGRID_API_KEY(SECRET),
  SENDGRID_API_URL(URL, "https://api.sendgrid.com/v3/mail/send"),
  SENDGRID_WELCOME_TEMPLATE_ID(STRING),
  NOTIFIER_SEND_TIMEOUT_IN_SECONDS(INT, "5");

  private final VariableType typ;
  private final String defaultValue;

  Variable(VariableType typ, String defaultValue) {
    this.typ = typ;
    if (typ == SECRET && defaultValue != null) {
      throw new IllegalStateException("A secret cannot have a default value.");
    }
    this.defaultValue = defaultValue;
  }

  Variable(VariableType typ) {
    this(typ, null);
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  @Override
  public String toString() {
    if (getDefaultValue() == null) {
      return this.name();
    }
    return this.name() + " (default=" + this.defaultValue + ")";
  }
}