package com.acsredux.adapter.filesystem;

import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.HashedPassword;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.LoginTime;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.MemberStatus;
import com.acsredux.core.members.values.RegistrationDate;
import com.acsredux.core.members.values.ZipCode;
import java.time.Instant;
import java.time.ZoneId;

public class MemberDTO {

  long id;
  String email;
  String firstName;
  String lastName;
  String zip;
  String status;
  String password;
  Long registeredOn;
  String tz;
  Long lastLogin;
  Long secondToLastLogin;
  int isAdmin;

  MemberDTO() {}

  MemberDTO(Member x) {
    this.id = x.id().val();
    this.email = x.email().val();
    this.firstName = x.firstName().val();
    this.lastName = x.lastName().val();
    this.zip = x.zip().val();
    this.status = x.status().name();
    this.password = x.password().val();
    this.registeredOn = x.registeredOn().val().getEpochSecond();
    this.tz = x.tz().getId();
    this.lastLogin = x.lastLogin().val().getEpochSecond();
    this.secondToLastLogin =
      x.secondToLastLogin() == null ? null : x.secondToLastLogin().val().getEpochSecond();
    this.isAdmin = x.isAdmin() ? 1 : 0;
  }

  Member asMember() {
    return new Member(
      new MemberID(id),
      new Email(email),
      new FirstName(firstName),
      new LastName(lastName),
      new ZipCode(zip),
      MemberStatus.valueOf(status),
      new HashedPassword(password),
      new RegistrationDate(Instant.ofEpochSecond(registeredOn)),
      ZoneId.of(tz),
      new LoginTime(Instant.ofEpochSecond(lastLogin)),
      secondToLastLogin == null
        ? null
        : new LoginTime(Instant.ofEpochSecond(secondToLastLogin)),
      isAdmin == 1
    );
  }
}
