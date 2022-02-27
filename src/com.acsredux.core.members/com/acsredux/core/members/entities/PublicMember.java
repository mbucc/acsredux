package com.acsredux.core.members.entities;

import com.acsredux.core.base.DateUtil;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.RegistrationDate;
import java.time.ZoneId;

/**
 * Member information that can be seen by anyone and everyone.
 */
public record PublicMember(
  MemberID id,
  FirstName firstName,
  ZoneId tz,
  RegistrationDate registeredOn
) {
  public String memberSince() {
    return DateUtil.fullDate(registeredOn.val(), tz);
  }

  public String fullName() {
    return firstName.val();
  }
}
