package com.acsredux.core.members.entities;

import com.acsredux.core.base.DateUtil;
import com.acsredux.core.members.values.*;
import java.time.ZoneId;

public record Member(
  MemberID id,
  Email email,
  FirstName firstName,
  LastName lastName,
  ZipCode zip,
  MemberStatus status,
  HashedPassword password,
  RegistrationDate registeredOn,
  ZoneId tz,
  LoginTime lastLogin,
  LoginTime secondToLastLogin
) {
  public String memberSince() {
    return DateUtil.fullDate(registeredOn.val(), tz);
  }

  public String fullName() {
    return firstName.val() + " " + lastName.val();
  }

  public Member withStatus(MemberStatus newStatus) {
    return new Member(
      id,
      email,
      firstName,
      lastName,
      zip,
      newStatus,
      password,
      registeredOn,
      tz,
      lastLogin,
      secondToLastLogin
    );
  }
}
