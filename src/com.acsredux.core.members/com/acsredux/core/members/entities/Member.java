package com.acsredux.core.members.entities;

import com.acsredux.core.base.DateUtil;
import com.acsredux.core.members.values.*;
import java.time.ZoneId;

public record Member(
  MemberID id,
  Email email,
  FirstName first,
  LastName last,
  ZipCode zip,
  MemberStatus status,
  EncryptedPassword password,
  RegistrationDate registeredOn,
  ZoneId tz
) {
  public String memberSince() {
    return DateUtil.fullDate(registeredOn.val(), tz);
  }
}
