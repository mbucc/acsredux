package com.acsredux.lib.env;

import static com.acsredux.lib.env.VariableType.INT;
import static com.acsredux.lib.env.VariableType.SECRET;
import static com.acsredux.lib.env.VariableType.STRING;
import static com.acsredux.lib.env.VariableType.URL;

public enum Variable {
  ACSREDUX_ACTIVATION_URL(URL),
  ACSREDUX_PASSWORD_SALT_FILENAME(STRING, "~/.acssalt"),
  ACSREDUX_ENCRYPTION_KEY_FILENAME(STRING, "~/.acsredux"),
  MAILGUN_API_KEY(SECRET),
  MAILGUN_API_URL(URL),
  NOTIFIER_SEND_TIMEOUT_IN_SECONDS(INT, "5"),
  IS_PRODUCTION(STRING, "N");

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
    String y = this.name();
    if (this.typ == SECRET) {
      y += " (encrypted)";
    }
    if (this.getDefaultValue() != null) {
      y += " (default=" + this.defaultValue + ")";
    }
    return y;
  }
}
