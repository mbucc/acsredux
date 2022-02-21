package com.acsredux.core.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import com.acsredux.core.auth.values.Action;
import com.acsredux.core.auth.values.Entitlement;
import com.acsredux.core.auth.values.Resource;
import com.acsredux.core.auth.values.User;
import org.junit.jupiter.api.Test;


class TestServicePolicy {

  private SecurityPolicyValidator validator;

  String readResource(String x) {
    String prefix = "resource '" + x + "'";
    InputStream y = getClass().getClassLoader().getResourceAsStream(x);
    try (y) {
      if (y == null) {
        throw new IllegalStateException(prefix + " not found");
      }
      return new String(y.readAllBytes());
    } catch (IOException e) {
      throw new IllegalStateException("error reading " + prefix, e);
    }
  }

//  @Test
//  void emptyPolicyRaisesException() throws Exception {
//    validator = new SecurityPolicyValidator("");
//    var y = validator.result();
//    assertFalse(y.isSuccessful(), y.value().raw().toString());
//    assertEquals("invalid policy", y.error().value());
//  }

  @Test
  void validPolicyIsParsed() throws Exception {
    // setup
    System.out.println("readResource(\"valid-policy1.json\") =\n" + readResource("valid-policy1.json"));
    validator = new SecurityPolicyValidator(readResource("valid-policy1.json"));

    // execute
    var y = validator.result();

    // verify
    assertTrue(y.isSuccessful(), y.error().value().toString());
    SecurityPolicy y1 = y.value().raw();
    assertEquals(1, y1.entitlements().entitlements().size());
    Entitlement y0 = y1.entitlements().entitlements().get(0);
    assertEquals(new Resource("article"), y0.resource());
    assertEquals(Action.CREATE, y0.action());
    assertEquals(new User("AuthenticatedMember"), y0.user());
  }

  @Test
  void invalidPolicyRaisesParseError() throws Exception {
    validator = new SecurityPolicyValidator(readResource("invalid-policy1.json"));
    var y = validator.result();
    assertFalse(y.isSuccessful(), y.value().raw().toString());
    assertEquals("invalid policy", y.error().value().toString());
  }
}
