package com.acsredux.core.auth.values;

import static java.lang.Math.min;

import com.acsredux.core.auth.SecurityPolicyException;
import com.google.gson.Gson;
import java.util.List;

public class SecurityPolicyDTO {

  public static class ACL {

    public String resource;
    public String action;
    public String user;

    public Entitlement asEntitlement() {
      return new Entitlement(
        ResourceSpec.of(resource),
        Action.valueOf(action.toUpperCase()),
        User.of(user)
      );
    }
  }

  public List<ACL> acls;

  public static SecurityPolicyDTO parse(String x) {
    if (x == null) {
      throw new SecurityPolicyException("null policy JSON");
    }
    if (x.isBlank()) {
      throw new SecurityPolicyException("empty policy JSON");
    }
    Gson gson = new Gson();
    try {
      return gson.fromJson(x, SecurityPolicyDTO.class);
    } catch (Exception e) {
      throw new RuntimeException(
        "can't parse: " + x.substring(0, min(50, x.length())),
        e
      );
    }
  }
}
