package com.acsredux.core.auth.values;

public record Entitlement(Resource resource, Action action, User user) {}
