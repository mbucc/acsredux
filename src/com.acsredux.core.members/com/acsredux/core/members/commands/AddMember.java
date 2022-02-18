package com.acsredux.core.members.commands;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.ZipCode;
import java.security.Principal;
import java.util.Objects;

public final class AddMember extends BaseMemberCommand {

  private final FirstName firstName;
  private final LastName lastName;
  private final Email email;
  private final ClearTextPassword password1;
  private final ClearTextPassword password2;
  private final ZipCode zipCode;

  public AddMember(
    Principal principal,
    FirstName firstName,
    LastName lastName,
    Email email,
    ClearTextPassword password1,
    ClearTextPassword password2,
    ZipCode zipCode
  ) {
    super(principal);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password1 = password1;
    this.password2 = password2;
    this.zipCode = zipCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AddMember addMember = (AddMember) o;
    return (
      firstName.equals(addMember.firstName) &&
      lastName.equals(addMember.lastName) &&
      email.equals(addMember.email) &&
      password1.equals(addMember.password1) &&
      password2.equals(addMember.password2) &&
      zipCode.equals(addMember.zipCode)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, email, password1, password2, zipCode);
  }

  @Override
  public String toString() {
    return (
      "AddMember{" +
      "firstName=" +
      firstName +
      ", lastName=" +
      lastName +
      ", email=" +
      email +
      ", password1=" +
      password1 +
      ", password2=" +
      password2 +
      ", zipCode=" +
      zipCode +
      "} " +
      super.toString()
    );
  }

  public FirstName firstName() {
    return firstName;
  }

  public LastName lastName() {
    return lastName;
  }

  public Email email() {
    return email;
  }

  public ClearTextPassword password1() {
    return password1;
  }

  public ClearTextPassword password2() {
    return password2;
  }

  public ZipCode zipCode() {
    return zipCode;
  }
}
