package com.acsredux.core.members.commands;

import com.acsredux.core.members.values.VerificationToken;
import java.security.Principal;
import java.util.Objects;

/**
 * A VerifyEmail command is submitted when a member clicks the
 * token link we sent in the welcome email.
 */
public final class VerifyEmail extends BaseMemberCommand {

  final VerificationToken token;

  public VerifyEmail(Principal principal, VerificationToken token) {
    super(principal);
    Objects.requireNonNull(token);
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    VerifyEmail that = (VerifyEmail) o;
    return token.equals(that.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), token);
  }

  @Override
  public String toString() {
    return "VerifyEmail{" + "token=" + token + "} " + super.toString();
  }

  public VerificationToken token() {
    return token;
  }
}
