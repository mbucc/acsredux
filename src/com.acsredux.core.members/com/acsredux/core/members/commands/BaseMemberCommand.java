package com.acsredux.core.members.commands;

import com.acsredux.core.base.BaseCommand;
import javax.security.auth.Subject;

public abstract sealed class BaseMemberCommand
  extends BaseCommand
  permits AddMember, LoginMember, VerifyEmail {

  BaseMemberCommand(Subject subject) {
    super(subject);
  }
}
