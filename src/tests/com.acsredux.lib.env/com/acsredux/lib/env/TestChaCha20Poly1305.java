package com.acsredux.lib.env;

import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64ToCharArray;
import static com.acsredux.lib.env.ChaCha20Poly1305.encryptToBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.getEncryptionKey;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestChaCha20Poly1305 {

  @BeforeEach
  void setup() {
    try {
      getEncryptionKey();
    } catch (Exception e) {
      fail("can't get key", e);
    }
  }

  @Test
  void testStringRoundTrip() throws Exception {
    // setup
    String pwd = "abc123";

    // execute
    String encrypted = encryptToBase64(pwd.getBytes(StandardCharsets.UTF_8));
    byte[] decrypted = decryptFromBase64(encrypted);

    // verify
    assertEquals(pwd, new String(decrypted));
  }

  @Test
  void testRoundTripASCIIOnly() throws Exception {
    // setup
    String pwd = "abc123";

    // execute
    String encrypted = encryptToBase64(pwd.getBytes(StandardCharsets.UTF_8));
    char[] decrypted = decryptFromBase64ToCharArray(encrypted);

    // verify
    assertArrayEquals(pwd.toCharArray(), decrypted);
  }

  @Test
  void testRoundTripNonAscii() throws Exception {
    // setup
    String pwd = "ab©123";

    // execute
    String encrypted = encryptToBase64(pwd.getBytes(StandardCharsets.UTF_8));
    char[] decrypted = decryptFromBase64ToCharArray(encrypted);

    // verify
    assertArrayEquals(pwd.toCharArray(), decrypted);
  }
}
