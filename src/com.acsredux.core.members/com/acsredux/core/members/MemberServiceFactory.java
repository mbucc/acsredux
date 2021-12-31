package com.acsredux.core.members;

import com.acsredux.core.members.ports.AdminReader;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.ports.Reader;
import com.acsredux.core.members.ports.Writer;
import com.acsredux.core.members.services.MemberProvider;
import java.time.Clock;
import java.time.InstantSource;
import java.time.ZoneId;

public final class MemberServiceFactory {

  private MemberServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static MemberService getMemberService(
    Reader r,
    Writer w,
    Notifier notifier,
    AdminReader adminReader,
    ZoneId tz
  ) {
    return getMemberService(r, w, notifier, adminReader, Clock.system(tz));
  }

  static MemberService getMemberService(
    Reader r,
    Writer w,
    Notifier notifier,
    AdminReader adminReader,
    InstantSource clock
  ) {
    return new MemberProvider(r, w, notifier, clock, adminReader);
  }
}
