package com.acsredux.core.members.services;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.commands.BaseMemberCommand;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.SessionID;
import java.security.SecureRandom;
import java.time.InstantSource;
import java.util.Base64;
import java.util.Optional;

public final class MemberServiceProvider implements MemberService {

  private final CreateHandler createHandler;
  private final VerifyEmailHandler verifyEmailHandler;
  private final LoginHandler loginHandler;
  private final MemberReader reader;
  private final MemberWriter writer;
  // SecureRandom self-seeds on the first call to nextBytes(), typically
  // from /dev/random (or /dev/urandom) in Linux.
  private final SecureRandom secureRandom = new SecureRandom();

  public MemberServiceProvider(
    MemberReader r,
    MemberWriter w,
    MemberNotifier notifier,
    InstantSource clock,
    MemberAdminReader adminReader
  ) {
    createHandler = new CreateHandler(r, adminReader, w, notifier, clock);
    verifyEmailHandler = new VerifyEmailHandler(r, w, clock);
    loginHandler = new LoginHandler(r, w, clock);

    this.reader = r;
    this.writer = w;
  }

  @Override
  public Event handle(BaseMemberCommand x) {
    return switch (x) {
      case CreateMember x1 -> createHandler.handle(x1);
      case VerifyEmail x1 -> verifyEmailHandler.handle(x1);
      case LoginMember x1 -> loginHandler.handle(x1);
    };
  }

  @Override
  public int activeMembers() {
    return reader.countActiveMembers();
  }

  @Override
  public Member getByID(MemberID x) {
    return reader.getByID(x);
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

  @Override
  public Optional<Member> findBySessionID(SessionID x) {
    return reader.findBySessionID(x);
  }
}
