package com.acsredux.lib.env;

import static com.acsredux.lib.env.ChaCha20Poly1305.decryptFromBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.encryptToBase64;
import static com.acsredux.lib.env.ChaCha20Poly1305.getKeyToBase64;
import static com.acsredux.lib.env.SecretFile.readSecretFromBase64;

public class Main {

  private Main() {
    throw new UnsupportedOperationException("static only");
  }

  // prettier-ignore
  static final String USAGE =
    "Commands:\n" + 
    "  -e <secret>          encrypt a secret\n" + 
    "  -d <encrypted>       decrypt a secret\n" + 
    "  -k                   generate a key to stdout\n" +
    "  -l                   list all environmental variables\n";

  static void die() {
    System.err.println(USAGE);
    System.exit(1);
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      die();
    }
    if (args[0].equals("-d")) {
      if (args.length < 2) {
        die();
      }
      System.out.println(new String(decryptFromBase64(args[1], readSecretFromBase64())));
    } else if (args[0].equals("-e")) {
      if (args.length < 2) {
        die();
      }
      try {
        System.out.println(
          encryptToBase64(args[1].getBytes("UTF8"), readSecretFromBase64())
        );
      } catch (Exception e) {
        System.err.println(e.getMessage());
        die();
      }
    } else if (args[0].equals("-k")) {
      System.out.print(getKeyToBase64());
    } else if (args[0].equals("-l")) {
      for (Variable x : Variable.values()) {
        System.out.println(x);
      }
    } else {
      die();
    }
  }
}
