package com.acsredux.core.members.services;

import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.EmailVerified;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import de.perschon.resultflow.Result;
import java.time.InstantSource;
import java.util.function.Function;

public class VerifyEmailHandler {

  private final MemberReader memberReader;
  private final MemberWriter memberWriter;
  private final InstantSource clock;

  public VerifyEmailHandler(
    MemberReader memberReader,
    MemberWriter memberWriter,
    InstantSource clock
  ) {
    this.memberReader = memberReader;
    this.memberWriter = memberWriter;
    this.clock = clock;
  }

  EmailVerified handle(VerifyEmail x) {
    Function<Member, EmailVerified> toEvent = o ->
      new EmailVerified(x, new CreatedOn(this.clock.instant()), o);
    return Result
      .ok(x)
      .map(VerifyEmail::token)
      .map(memberReader::getByToken)
      .map(id -> memberWriter.updateStatus(id, MemberStatus.ACTIVE))
      .map(memberReader::getByID)
      .map(toEvent)
      .get();
  }
}
