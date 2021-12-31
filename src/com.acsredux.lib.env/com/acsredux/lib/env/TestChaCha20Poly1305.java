package com.acsredux.lib.env;

import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64ToCharArray;
import static com.acsredux.lib.env.ChaCha20Poly1305.encryptToBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.getKey;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class TestChaCha20Poly1305 {

  @Test
  void testStringRoundTrip() throws Exception {
    // setup
    String pwd = "abc123";
    SecretKey key = getKey();

    // execute
    String encrypted = encryptToBase64(pwd.getBytes("UTF8"), key);
    byte[] decrypted = decryptFromBase64(encrypted, key);

    // verify
    assertEquals(pwd, new String(decrypted));
  }

  @Test
  void testRoundTripASCIIOnly() throws Exception {
    // setup
    String pwd = "abc123";
    SecretKey key = getKey();

    // execute
    String encrypted = encryptToBase64(pwd.getBytes("UTF8"), key);
    char[] decrypted = decryptFromBase64ToCharArray(encrypted, key);

    // verify
    assertArrayEquals(pwd.toCharArray(), decrypted);
  }

  @Test
  void testRoundTripNonAscii() throws Exception {
    // setup
    String pwd = "ab©123";
    SecretKey key = getKey();

    // execute
    String encrypted = encryptToBase64(pwd.getBytes("UTF8"), key);
    char[] decrypted = decryptFromBase64ToCharArray(encrypted, key);

    // verify
    assertArrayEquals(pwd.toCharArray(), decrypted);
  }
}
