package com.acsredux.lib.env;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecretFile {

  private SecretFile() {
    throw new UnsupportedOperationException("static only");
  }

  public static SecretKey readSecretFromBase64() {
    String envvar = Variable.ACSREDUX_SECRET_FILENAME.name();
    String fn = System.getenv(envvar);
    final byte[] key;
    try {
      Path p = Path.of(fn);
      key = Base64.getDecoder().decode(Files.readAllBytes(p));
    } catch (Exception e) {
      String fmt = "error reading secret from %s (%s)";
      throw new IllegalStateException(String.format(fmt, envvar, fn), e);
    }
    return new SecretKeySpec(key, "ChaCha20-Poly1305");
  }
}
