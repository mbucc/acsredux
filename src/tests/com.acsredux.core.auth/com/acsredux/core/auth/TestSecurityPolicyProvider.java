package com.acsredux.core.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.acsredux.core.auth.values.*;
import com.spencerwi.either.Result;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class TestSecurityPolicyProvider {

  String readResource(String x) {
    String prefix = "resource '" + x + "'";
    InputStream y = getClass().getClassLoader().getResourceAsStream(x);
    try (y) {
      if (y == null) {
        String cwd = System.getProperty("user.dir");
        String cp = System.getProperty("java.class.path");
        String msg = String.format(
          "%s not found, with cwd=%s and classpath=\n%s",
          prefix,
          cwd,
          String.join("\n", cp.split("[;:]"))
        );
        throw new IllegalStateException(msg);
      }
      return new String(y.readAllBytes());
    } catch (IOException e) {
      throw new IllegalStateException("error reading " + prefix, e);
    }
  }

  @Test
  void emptyPolicyFails() {
    // execute
    Result<SecurityPolicyProvider> y = SecurityPolicyProvider.parse("");

    // verify
    if (y.isOk()) {
      fail(y.getResult().toString());
    }
    assertTrue(y.isErr());
    assertTrue(y.getException() instanceof SecurityPolicyException);
    assertEquals("empty policy JSON", y.getException().getMessage());
  }

  @Test
  void validPolicyIsParsed() throws Exception {
    // execute
    Result<SecurityPolicyProvider> y = SecurityPolicyProvider.parse(
      readResource("policy-ok1.json")
    );

    // verify
    if (y.isErr()) {
      throw y.getException();
    }
    assertTrue(y.isOk());
    SecurityPolicyProvider y1 = y.getResult();
    assertEquals(1, y1.entitlements().size());
    Entitlement y0 = y1.entitlements().get(0);
    assertEquals(new ResourceSpec("articles", Resource.ARTICLES), y0.resource());
    assertEquals(Action.CREATE, y0.action());
    assertEquals(new User("AuthenticatedMember"), y0.user());
  }

  @Test
  void invalidPolicyRaisesParseError() {
    // execute
    Result<SecurityPolicyProvider> y = SecurityPolicyProvider.parse(
      readResource("policy-bad1.json")
    );

    // verify
    if (y.isOk()) {
      fail(y.getResult().toString());
    }
    assertTrue(y.isErr());
    assertEquals(
      "No enum constant com.acsredux.core.auth.values.Action.FOOBAR",
      y.getException().getMessage()
    );
  }
}
