package com.acsredux.core.members.commands;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;
import java.util.Objects;
import javax.security.auth.Subject;

public final class LoginMember extends BaseMemberCommand {

  private final Email email;
  private final ClearTextPassword password;

  public LoginMember(Subject subject, Email email, ClearTextPassword password) {
    super(subject);
    Objects.requireNonNull(email);
    Objects.requireNonNull(password);
    this.email = email;
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoginMember that = (LoginMember) o;
    return email.equals(that.email) && password.equals(that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), email, password);
  }

  @Override
  public String toString() {
    return (
      "LoginMember{" +
      "email=" +
      email +
      ", password=" +
      password +
      "} " +
      super.toString()
    );
  }

  public Email email() {
    return email;
  }

  public ClearTextPassword password() {
    return password;
  }
}
