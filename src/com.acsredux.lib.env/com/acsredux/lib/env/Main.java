package com.acsredux.lib.env;

import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.encryptToBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.getKeyToBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.getSaltToBase64;

import java.nio.charset.StandardCharsets;

/**
 * A command-line interface for managing environmental variables, including encrypted secrets.
 * <p>
 * The encryption uses the ChaCha20-Poly1305 algorithm, new in Java 11.
 *
 * @see <a href="https://openjdk.java.net/jeps/329">JEP 329</a>
 * @see <a href="https://mkyong.com/java/java-11-chacha20-poly1305-encryption-examples/">Java 11 – ChaCha20-Poly1305 encryption examples</a>
 */
public class Main {

  private Main() {
    throw new UnsupportedOperationException("static only");
  }

  // prettier-ignore
  static final String USAGE = """
    Commands:
      -e <secret>          encrypt a secret
      -d <encrypted>       decrypt a secret
      -k                   print an encryption key to stdout (256-bit, base64 encoded)
      -s                   print a password salt to stdout (512-bit, base64 encoded)
      -l                   list all environmental variables
    """;

  static void die() {
    System.err.println(USAGE);
    System.exit(1);
  }

  /**
   * The supported commands are:
   * <pre>{@code
   * -e <secret>          encrypt a secret
   * -d <encrypted>       decrypt a secret
   * -k                   print an encryption key to stdout (256-bit, base64 encoded)
   * -s                   print a password salt to stdout (512-bit, base64 encoded)
   * -l                   list all environmental variables
   * }</pre>
   *
   * <p>
   * The shell script used in the example below is <code>env.sh</code>:
   * <pre>
   * #!/bin/sh -e
   *
   * java -p mlib -m com.acsredux.lib.env "$&#64;"
   * </pre>
   *
   * <p>
   * In a typical usage, you would start by listing the variables.
   *
   * <pre>{@code
   * $ ./env.sh -l
   * ACSREDUX_ACTIVATION_URL
   * ACSREDUX_SALT_FILENAME (default=~/.acssalt)
   * ACSREDUX_SECRET_FILENAME (default=~/.acsredux)
   * MAILGUN_API_KEY (encrypted)
   * MAILGUN_API_URL
   * NOTIFIER_SEND_TIMEOUT_IN_SECONDS (default=5)
   * }</pre>
   *
   * <p>
   * Create a salt used for the one-way password hashing.
   *
   * <pre>{@code
   * $ echo export ACSREDUX_SALT_FILENAME=.mysalt > .myenv
   * $ source .myenv
   * $ ./env.sh -s > $ACSREDUX_SALT_FILENAME
   * $ chmod 400 $ACSREDUX_SALT_FILENAME
   * }</pre>
   *
   * <p>
   * Create a key for symmetric encryption.
   *
   * <pre>{@code
   * $ echo export ACSREDUX_SECRET_FILENAME=.mykey >> .myenv
   * $ source .myenv
   * $ ./env.sh -k > $ACSREDUX_SECRET_FILENAME
   * $ chmod 400 $ACSREDUX_SECRET_FILENAME
   * }</pre>
   *
   * <p>
   * Encrypt a password with the key stored in ACSREDUX_SECRET_FILENAME.
   *
   * <pre>{@code
   * $ echo export MAILGUN_API_KEY=$(./env.sh -e abc123) >> .myenv
   * $ source .myenv
   * }</pre>
   *
   * <p>
   * We can verify the steps were correct by decrypting the password.
   *
   * <pre>{@code
   * $ ./env.sh -d $MAILGUN_API_KEY
   * abc123
   * }</pre>
   *
   * <p>
   * The encryption key and password salt are retrieved at startup, so if you want to more tightly
   * control access to the encryption secret, you could have your startup script run as root and
   * take the following steps:
   * <ol>
   *  <li>As root, copy secret file to location that the acsredux user expects it.
   *  <li>Start the service as the acsredux user with <code>su</code>.
   *  <li>When the service is up, delete the secret file.
   * </ol>
   *
   * <p>
   * With this approach, a local exploit alone will not reveal your encryption key.
   *
   * @see javax.crypto.KeyGenerator#getInstance(String)
   * @see javax.crypto.KeyGenerator#init(int)
   * @see javax.crypto.KeyGenerator#generateKey()
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      die();
    }
    switch (args[0]) {
      case "-d":
        if (args.length < 2) {
          die();
        }
        System.out.println(new String(decryptFromBase64(args[1])));
        break;
      case "-e":
        if (args.length < 2) {
          die();
        }
        try {
          System.out.println(encryptToBase64(args[1].getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
          System.err.println(e.getMessage());
          die();
        }
        break;
      case "-k":
        System.out.print(getKeyToBase64());
        break;
      case "-s":
        // From what I read, it doesn't help to have a salt that is
        // longer than the hash size, which is 512 bits (or 64 bytes)
        // for PBKDF2WithHmacSHA512.
        System.out.print(getSaltToBase64(64));
        break;
      case "-l":
        for (Variable x : Variable.values()) {
          System.out.println(x);
        }
        break;
      default:
        die();
        break;
    }
  }
}
