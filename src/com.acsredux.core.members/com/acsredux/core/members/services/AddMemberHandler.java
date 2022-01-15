package com.acsredux.core.members.services;

import static com.acsredux.core.base.Util.dieIfNull;

import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.AdminReader;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.ports.Writer;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.VerificationToken;
import java.time.InstantSource;
import java.util.Optional;
import java.util.ResourceBundle;

public final class AddMemberHandler {

  private final MemberReader reader;
  private final AdminReader adminMemberReader;
  private final Writer writer;
  private final Notifier notifier;
  private final InstantSource clock;
  private final ResourceBundle msgs;

  public AddMemberHandler(
    MemberReader reader,
    AdminReader adminMemberReader,
    Writer writer,
    Notifier notifier,
    InstantSource clock
  ) {
    this.reader = reader;
    this.adminMemberReader = adminMemberReader;
    this.writer = writer;
    this.notifier = notifier;
    this.clock = clock;
    this.msgs = ResourceBundle.getBundle("MemberErrorMessages");
  }

  Accum validateAddMember(AddMember x) {
    dieIfNull(x.email(), msgs.getString("email_missing"));
    dieIfNull(x.password1(), msgs.getString("password1_missing"));
    dieIfNull(x.password2(), msgs.getString("password2_missing"));
    if (!x.password1().equals(x.password2())) {
      throw new ValidationException(msgs.getString("password_mismatch"));
    }
    checkEmailIsUnique(x.email());
    return new Accum(x, new CreatedOn(this.clock.instant()));
  }

  void checkEmailIsUnique(Email x) {
    if (reader.findByEmail(x).isPresent()) {
      throw new ValidationException(msgs.getString("email_taken"));
    }
  }

  Accum addMember(Accum x) {
    x.newMemberID = writer.addMember(x.cmd, x.createdOn);
    return x;
  }

  Accum getToken(Accum x) {
    x.token = writer.addAddMemberToken(x.newMemberID, x.createdOn);
    return x;
  }

  MemberAdded toMemberAdded(Accum x) {
    return new MemberAdded(x.cmd, x.createdOn, x.token, x.newMemberID);
  }

  MemberAdded notify(MemberAdded x) {
    notifier.memberAdded(x, adminMemberReader.getSiteInfo());
    return x;
  }

  private static class Accum {

    AddMember cmd;
    MemberID newMemberID;
    VerificationToken token;
    CreatedOn createdOn;

    Accum(AddMember cmd, CreatedOn createdOn) {
      this.cmd = cmd;
      this.createdOn = createdOn;
    }
  }

  MemberAdded handle(AddMember x) {
    return Optional
      .of(x)
      .map(this::validateAddMember)
      .map(this::addMember)
      .map(this::getToken)
      .map(this::toMemberAdded)
      .map(this::notify)
      .get();
  }
}
