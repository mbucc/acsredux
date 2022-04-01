package com.acsredux.core.members.services;

import static com.acsredux.core.members.PasswordUtil.checkpw;

import com.acsredux.core.base.NotAuthorizedException;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberLoggedIn;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.LoginTime;
import de.perschon.resultflow.Result;
import java.time.Instant;
import java.time.InstantSource;

public class LoginHandler {

  private final MemberReader memberReader;
  private final MemberWriter memberWriter;
  private final InstantSource clock;

  public LoginHandler(
    MemberReader memberReader,
    MemberWriter memberWriter,
    InstantSource clock
  ) {
    this.memberReader = memberReader;
    this.memberWriter = memberWriter;
    this.clock = clock;
  }

  Member validatePassword(LoginMember x1, Member x2) {
    if (!checkpw(x1.password(), x2.password())) {
      throw new NotAuthorizedException("Invalid password");
    }
    return x2;
  }

  Member setLastLogin(LoginTime x1, Member x2) {
    memberWriter.setLastLogin(x2.id(), x1);
    return x2;
  }

  MemberLoggedIn handle(LoginMember x) {
    Instant now = this.clock.instant();
    return Result
      .ok(x)
      .map(LoginMember::email)
      .map(memberReader::getByEmail)
      .map(m -> validatePassword(x, m))
      .map(m -> setLastLogin(LoginTime.of(now), m))
      .map(m -> new MemberLoggedIn(x, new CreatedOn(now), m))
      .get();
  }
}
