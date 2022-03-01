package com.acsredux.core.members.commands;

import com.acsredux.core.base.Command;

public sealed interface BaseMemberCommand
  extends Command
  permits CreateMember, LoginMember, VerifyEmail {}
