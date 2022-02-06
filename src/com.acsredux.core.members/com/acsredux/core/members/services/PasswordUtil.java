package com.acsredux.core.members.services;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.HashedPassword;
import com.acsredux.lib.env.VariableUtil;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Make class public so tests can use hashpw().
public class PasswordUtil {

  private static final int ITERATIONS = 120000;
  private static final int KEY_LENGTH = 512;
  private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

  private static final byte[] SALT = VariableUtil.readPasswordSalt();

  private PasswordUtil() {
    throw new UnsupportedOperationException("static only");
  }

  public static HashedPassword hashpw(ClearTextPassword x) {
    PBEKeySpec spec = new PBEKeySpec(x.val(), SALT, ITERATIONS, KEY_LENGTH);
    try {
      SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
      byte[] securePassword = fac.generateSecret(spec).getEncoded();
      return new HashedPassword(Base64.getEncoder().encodeToString(securePassword));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      throw new RuntimeException(ex);
    } finally {
      spec.clearPassword();
    }
  }

  static boolean checkpw(ClearTextPassword x1, HashedPassword x2) {
    HashedPassword y = hashpw(x1);
    return Objects.equals(x2, y);
  }

  public static void passwordSaltOrDie() {
    if (SALT == null) {
      throw new IllegalStateException("password salt not found.");
    }
  }
}
