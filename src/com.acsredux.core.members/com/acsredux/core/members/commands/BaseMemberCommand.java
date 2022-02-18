package com.acsredux.core.members.commands;

import com.acsredux.core.base.BaseCommand;
import java.security.Principal;

public abstract sealed class BaseMemberCommand
  extends BaseCommand
  permits AddMember, LoginMember, VerifyEmail {

  BaseMemberCommand(Principal principal) {
    super(principal);
  }
}
