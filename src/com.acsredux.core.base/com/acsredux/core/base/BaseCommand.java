package com.acsredux.core.base;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import javax.security.auth.Subject;

public class BaseCommand implements Command {

  private final Instant created;
  private final UUID guid;
  private final Subject subject;

  public BaseCommand(Subject subject) {
    Objects.requireNonNull(subject);
    this.created = Instant.now();
    this.guid = java.util.UUID.randomUUID();
    this.subject = subject;
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
  public Subject subject() {
    return subject;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseCommand that = (BaseCommand) o;
    return (
      created.equals(that.created) &&
      guid.equals(that.guid) &&
      subject.equals(that.subject)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, guid, subject);
  }

  @Override
  public String toString() {
    return (
      "BaseCommand{" +
      "created=" +
      created +
      ", guid=" +
      guid +
      ", subject=" +
      subject +
      '}'
    );
  }
}
