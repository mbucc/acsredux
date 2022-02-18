package com.acsredux.core.base;

import java.security.Principal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class BaseCommand implements Command {

  private final Instant created;
  private final UUID guid;
  private final Principal principal;

  public BaseCommand(Principal principal) {
    Objects.requireNonNull(principal);
    this.created = Instant.now();
    this.guid = java.util.UUID.randomUUID();
    this.principal = principal;
  }

  @Override
  public Instant created() {
    return created;
  }

  @Override
  public UUID guid() {
    return guid;
  }

  @Override
  public Principal principal() {
    return principal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseCommand that = (BaseCommand) o;
    return (
      created.equals(that.created) &&
      guid.equals(that.guid) &&
      principal.equals(that.principal)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, guid, principal);
  }

  @Override
  public String toString() {
    return (
      "BaseCommand{" +
      "created=" +
      created +
      ", guid=" +
      guid +
      ", principal=" +
      principal +
      '}'
    );
  }
}
