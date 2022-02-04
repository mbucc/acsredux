package com.acsredux.core.members.services;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.commands.MemberCommand;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.SessionID;
import java.security.SecureRandom;
import java.time.InstantSource;
import java.util.Base64;
import java.util.Optional;

public final class MemberProvider implements MemberService {

  private final AddMemberHandler addMemberHandler;
  private final VerifyEmailHandler verifyEmailHandler;
  private final MemberReader reader;
  private final MemberWriter writer;
  private final MemberAdminReader adminReader;
  // SecureRandom self-seeds on the first call to nextBytes(), typically
  // from /dev/random (or /dev/urandom) in Linux.
  private final SecureRandom secureRandom = new SecureRandom();

  public MemberProvider(
    MemberReader r,
    MemberWriter w,
    MemberNotifier notifier,
    InstantSource clock,
    MemberAdminReader adminReader
  ) {
    addMemberHandler = new AddMemberHandler(r, adminReader, w, notifier, clock);
    verifyEmailHandler = new VerifyEmailHandler(r, w, clock);
    this.reader = r;
    this.writer = w;
    this.adminReader = adminReader;
  }

  @Override
  public Event handle(MemberCommand x) {
    if (x instanceof AddMember c) {
      return addMemberHandler.handle(c);
    } else if (x instanceof VerifyEmail c) {
      return verifyEmailHandler.handle(c);
    } else {
      throw new IllegalArgumentException("invalid command " + x);
    }
  }

  @Override
  public Optional<MemberDashboard> findDashboard(MemberID x) {
    return reader.findMemberDashboard(x);
  }

  @Override
  public MemberDashboard getDashboard(MemberID x) {
    return reader
      .findMemberDashboard(x)
      .orElseThrow(() ->
        new NotFoundException("Member ID " + x.val() + " is not on file.")
      );
  }

  @Override
  public int activeMembers() {
    return reader.countActiveMembers();
  }

  @Override
  public SessionID createSessionID(MemberID x) {
    // OWASP recommends a session ID be at least 128 (16 bytes) long [1].
    // https://owasp.org/www-community/vulnerabilities/Insufficient_Session-ID_Length
    byte[] ys = new byte[128];

    // This will block on first call as system provides entropy.
    secureRandom.nextBytes(ys);
    var y = new SessionID(Base64.getEncoder().encodeToString(ys));
    writer.writeSessionID(x, y);
    return y;
  }
}
