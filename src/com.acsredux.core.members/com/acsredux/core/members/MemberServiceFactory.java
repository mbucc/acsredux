package com.acsredux.core.members;

import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.services.MemberProvider;
import java.time.Clock;
import java.time.InstantSource;
import java.time.ZoneId;

public final class MemberServiceFactory {

  private MemberServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static MemberService getMemberService(
    MemberReader r,
    MemberWriter w,
    MemberNotifier notifier,
    MemberAdminReader adminReader,
    ZoneId tz
  ) {
    return getMemberService(r, w, notifier, adminReader, Clock.system(tz));
  }

  static MemberService getMemberService(
    MemberReader r,
    MemberWriter w,
    MemberNotifier notifier,
    MemberAdminReader adminReader,
    InstantSource clock
  ) {
    return new MemberProvider(r, w, notifier, clock, adminReader);
  }
}
