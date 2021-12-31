package com.acsredux.lib.env;

import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64ToCharArray;
import static com.acsredux.lib.env.SecretFile.readSecretFromBase64;

import java.net.URI;
import java.util.Map;

public class Util {

  private Util() {
    throw new UnsupportedOperationException("static only");
  }

  public static URI getURI(Variable x, Map<String, String> env) {
    String y = get(x, env);
    if (y == null && x.getDefaultValue() != null) {
      y = x.getDefaultValue();
    }
    try {
      return new URI(y);
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  public static int getInt(Variable x, Map<String, String> env) {
    String y = get(x, env);
    if (y == null && x.getDefaultValue() != null) {
      y = x.getDefaultValue();
    }
    try {
      return Integer.valueOf(y);
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  public static String getString(Variable x, Map<String, String> env) {
    try {
      String y = get(x, env);
      if (y == null && x.getDefaultValue() != null) {
        y = x.getDefaultValue();
      }
      return y;
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  public static char[] getSecret(Variable x, Map<String, String> env) {
    try {
      return decryptFromBase64ToCharArray(get(x, env), readSecretFromBase64());
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  static IllegalStateException die(Variable x, Exception e, Map<String, String> env) {
    return die(x, e, env, false);
  }

  static IllegalStateException die(
    Variable x,
    Exception e,
    Map<String, String> env,
    boolean noEcho
  ) {
    String msg = String.format(
      "Error loading environmental variable %s (%s).",
      x.name(),
      (noEcho ? "******" : get(x, env))
    );
    return new IllegalStateException(msg, e);
  }

  private static String get(Variable x, Map<String, String> env) {
    return env.get(x.name());
  }
}
