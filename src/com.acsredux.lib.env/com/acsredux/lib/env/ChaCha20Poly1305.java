//  Copyright (c) 2020 Mkyong.com
//  Copyright (c) 2022 Mark Bucciarelli <mkbucc@gmail.com>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the “Software”), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package com.acsredux.lib.env;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class ChaCha20Poly1305 {

  private static final String CHACHA = "ChaCha20";
  private static final String ENCRYPT_ALGO = "ChaCha20-Poly1305";
  private static final int NONCE_LEN = 12; // 96 bits, 12 bytes

  private static final SecretKey ENCRYPTION_KEY = new SecretKeySpec(
    Base64.getDecoder().decode(VariableUtil.readEncryptionKey()),
    "ChaCha20-Poly1305"
  );

  static boolean keyExists() {
    return ENCRYPTION_KEY != null;
  }

  private ChaCha20Poly1305() {
    throw new UnsupportedOperationException("static only");
  }

  public static String encryptToBase64(byte[] pText) throws Exception {
    return Base64.getEncoder().encodeToString(encrypt(pText));
  }

  public static byte[] decryptFromBase64(String encrypted) throws Exception {
    return decrypt(Base64.getDecoder().decode(encrypted));
  }

  public static char[] decryptFromBase64ToCharArray(String encrypted) throws Exception {
    byte[] xs = decryptFromBase64(encrypted);
    var decoder = StandardCharsets.UTF_8.newDecoder();
    var charBuffer = decoder.decode(ByteBuffer.wrap(xs));
    var ys = new char[charBuffer.length()];
    for (int i = 0; i < charBuffer.length(); i++) {
      ys[i] = charBuffer.get(i);
    }
    return ys;
  }

  public static String getKeyToBase64() throws Exception {
    return Base64.getEncoder().encodeToString(getEncryptionKey().getEncoded());
  }

  public static String getSaltToBase64(int saltSizeInBytes) {
    if (saltSizeInBytes < 1) {
      throw new IllegalArgumentException("salt size must be greater than zero");
    }
    byte[] salt = new byte[saltSizeInBytes];
    new SecureRandom().nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  public static SecretKey getEncryptionKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance(CHACHA);
    keyGen.init(256, SecureRandom.getInstanceStrong());
    return keyGen.generateKey();
  }

  // if no nonce, generate a random 12 bytes nonce
  public static byte[] encrypt(byte[] pText) throws Exception {
    return encrypt(pText, getNonce());
  }

  public static byte[] encrypt(byte[] pText, byte[] nonce) throws Exception {
    Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

    // IV, initialization value with nonce
    IvParameterSpec iv = new IvParameterSpec(nonce);

    cipher.init(Cipher.ENCRYPT_MODE, ChaCha20Poly1305.ENCRYPTION_KEY, iv);

    byte[] encryptedText = cipher.doFinal(pText);

    // append nonce to the encrypted text
    return ByteBuffer
      .allocate(encryptedText.length + NONCE_LEN)
      .put(encryptedText)
      .put(nonce)
      .array();
  }

  public static byte[] decrypt(byte[] cText) throws Exception {
    ByteBuffer bb = ByteBuffer.wrap(cText);

    // split cText to get the appended nonce
    if (cText.length <= NONCE_LEN) {
      throw new IllegalStateException("too few bytes to decrypt");
    }
    byte[] encryptedText = new byte[cText.length - NONCE_LEN];
    byte[] nonce = new byte[NONCE_LEN];
    bb.get(encryptedText);
    bb.get(nonce);

    Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

    IvParameterSpec iv = new IvParameterSpec(nonce);

    cipher.init(Cipher.DECRYPT_MODE, ChaCha20Poly1305.ENCRYPTION_KEY, iv);

    // decrypted text
    return cipher.doFinal(encryptedText);
  }

  // 96-bit nonce (12 bytes)
  private static byte[] getNonce() {
    byte[] newNonce = new byte[12];
    new SecureRandom().nextBytes(newNonce);
    return newNonce;
  }
}
