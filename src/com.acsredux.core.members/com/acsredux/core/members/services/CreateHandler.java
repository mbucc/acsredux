package com.acsredux.core.members.services;

import static com.acsredux.core.members.MemberService.ANONYMOUS_USERNAME;

import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import java.time.InstantSource;
import java.util.Optional;
import java.util.ResourceBundle;

public final class CreateHandler {

  private final MemberReader reader;
  private final MemberAdminReader adminReader;
  private final MemberWriter writer;
  private final MemberNotifier notifier;
  private final InstantSource clock;
  private final ResourceBundle msgs;

  public CreateHandler(
    MemberReader reader,
    MemberAdminReader adminReader,
    MemberWriter writer,
    MemberNotifier notifier,
    InstantSource clock
  ) {
    this.reader = reader;
    this.adminReader = adminReader;
    this.writer = writer;
    this.notifier = notifier;
    this.clock = clock;
    this.msgs = ResourceBundle.getBundle("MemberErrorMessages");
  }

  Accum validateAddMember(CreateMember x) {
    if (!x.password1().equals(x.password2())) {
      throw new ValidationException(msgs.getString("password_mismatch"));
    }
    checkEmailIsUnique(x.email());
    checkNameIsUnique(x.firstName(), x.lastName());
    checkNameIsNotAnonymousUsername(x.firstName(), x.lastName());
    return new Accum(x, new CreatedOn(this.clock.instant()));
  }

  void checkEmailIsUnique(Email x) {
    if (reader.findByEmail(x).isPresent()) {
      throw new ValidationException(msgs.getString("email_taken"));
    }
  }

  void checkNameIsNotAnonymousUsername(FirstName x1, LastName x2) {
    String combined = x1.val().trim() + " " + x2.val().trim();
    if (combined.equalsIgnoreCase(ANONYMOUS_USERNAME)) {
      throw new ValidationException(msgs.getString("name_is_anonymous_username"));
    }
  }

  void checkNameIsUnique(FirstName x1, LastName x2) {
    if (reader.findByName(x1, x2).isPresent()) {
      throw new ValidationException(msgs.getString("name_taken"));
    }
  }

  Accum addMember(Accum x) {
    x.newMemberID =
      writer.addMember(x.cmd, MemberStatus.NEEDS_EMAIL_VERIFICATION, x.createdOn);
    return x;
  }

  Accum createAndWriteValidationToken(Accum x) {
    x.token = writer.addAddMemberToken(x.newMemberID, x.createdOn);
    return x;
  }

  MemberAdded toMemberAdded(Accum x) {
    return new MemberAdded(x.cmd, x.createdOn, x.token, x.newMemberID);
  }

  MemberAdded notify(MemberAdded x) {
    notifier.memberAdded(x, adminReader.getSiteInfo());
    return x;
  }

  private static class Accum {

    final CreateMember cmd;
    MemberID newMemberID;
    VerificationToken token;
    final CreatedOn createdOn;

    Accum(CreateMember cmd, CreatedOn createdOn) {
      this.cmd = cmd;
      this.createdOn = createdOn;
    }
  }

  MemberAdded handle(CreateMember x) {
    return Optional
      .of(x)
      .map(this::validateAddMember)
      .map(this::addMember)
      .map(this::createAndWriteValidationToken)
      .map(this::toMemberAdded)
      .map(this::notify)
      .get();
  }
}
