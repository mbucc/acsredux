package com.acsredux.core.members.commands;

import com.acsredux.core.base.Command;

public sealed interface MemberCommand extends Command permits AddMember, VerifyEmail {}
