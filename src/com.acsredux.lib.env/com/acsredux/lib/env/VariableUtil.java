package com.acsredux.lib.env;

import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64ToCharArray;
import static com.acsredux.lib.env.Variable.ACSREDUX_ENCRYPTION_KEY_FILENAME;
import static com.acsredux.lib.env.Variable.ACSREDUX_PASSWORD_SALT_FILENAME;
import static java.util.Objects.requireNonNullElse;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class VariableUtil {

  private VariableUtil() {
    throw new UnsupportedOperationException("static only");
  }

  /**
   * Throw an IllegalStateException if the encryption key is not found.
   * <p>
   * Some classes need the key to be present (e.g., the MailGun adapter),
   * and some do not (e.g., the command-line env utility).
   * This method provides a way for a class to halt if the key is not found.
   *
   * @see Main#main
   * @see com.acsredux.adapter.mailgun.MailgunNotifier#MailgunNotifier()
   */
  public static void encryptionKeyOrDie() {
    if (!ChaCha20Poly1305.keyExists()) {
      throw new IllegalStateException("encryption key not found.");
    }
  }

  public static URI getURI(Variable x, Map<String, String> env) {
    try {
      return new URI(requireNonNullElse(get(x, env), x.getDefaultValue()));
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  public static int getInt(Variable x, Map<String, String> env) {
    try {
      return Integer.parseInt(requireNonNullElse(get(x, env), x.getDefaultValue()));
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  public static String getString(Variable x, Map<String, String> env) {
    try {
      return requireNonNullElse(get(x, env), x.getDefaultValue());
    } catch (Exception e) {
      throw die(x, e, env);
    }
  }

  public static char[] getSecret(Variable x, Map<String, String> env) {
    try {
      return decryptFromBase64ToCharArray(get(x, env));
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

  public static byte[] readPasswordSalt() {
    return readFile(ACSREDUX_PASSWORD_SALT_FILENAME);
  }

  public static byte[] readEncryptionKey() {
    return readFile(ACSREDUX_ENCRYPTION_KEY_FILENAME);
  }

  public static byte[] readFile(Variable x) {
    String fn = VariableUtil.getString(x, System.getenv());
    if (fn.startsWith("~" + File.separator)) {
      fn = System.getProperty("user.home") + fn.substring(1);
    } else if (fn.startsWith("~")) {
      throw new UnsupportedOperationException(
        "Please use an absolute path to indicate a user's home directory."
      );
    }
    try {
      return Files.readAllBytes(Path.of(fn));
    } catch (Exception e) {
      String fmt = "error reading '%s' with cwd = %s";
      throw new RuntimeException(
        String.format(fmt, fn, System.getProperty("user.dir")),
        e
      );
    }
  }
}
