package com.acsredux.core.auth;

import static com.acsredux.core.base.Util.readResource;
import static org.junit.jupiter.api.Assertions.*;

import com.acsredux.core.auth.values.Action;
import com.acsredux.core.auth.values.Entitlement;
import com.acsredux.core.auth.values.Resource;
import com.acsredux.core.auth.values.ResourceSpec;
import com.acsredux.core.auth.values.User;
import org.junit.jupiter.api.Test;

class TestSecurityPolicyProvider {

  @Test
  void emptyPolicyFails() {
    // execute
    SecurityPolicyException y = assertThrows(
      SecurityPolicyException.class,
      () -> SecurityPolicyProvider.parse("")
    );

    // verify
    assertEquals("empty policy JSON", y.getMessage());
  }

  @Test
  void validPolicyIsParsed() {
    // execute
    SecurityPolicyProvider y = assertDoesNotThrow(() ->
      SecurityPolicyProvider.parse(readResource("policy-ok1.json"))
    );

    // verify
    assertEquals(1, y.entitlements().size());
    Entitlement y0 = y.entitlements().get(0);
    assertEquals(new ResourceSpec("articles", Resource.ARTICLES), y0.resource());
    assertEquals(Action.CREATE, y0.action());
    assertEquals(new User("AuthenticatedMember"), y0.user());
  }

  @Test
  void invalidPolicyRaisesParseError() {
    // execute
    SecurityPolicyException y = assertThrows(
      SecurityPolicyException.class,
      () -> SecurityPolicyProvider.parse(readResource("policy-bad1.json"))
    );

    // verify
    assertEquals(
      "java.lang.IllegalArgumentException: No enum constant com.acsredux.core.auth.values.Action.FOOBAR",
      y.getMessage()
    );
  }
}
