package com.acsredux.core.auth.values;

public record Entitlement(ResourceSpec resource, Action action, User user) {}
