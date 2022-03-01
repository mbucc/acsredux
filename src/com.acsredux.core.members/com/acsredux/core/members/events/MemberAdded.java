package com.acsredux.core.members.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.VerificationToken;

public record MemberAdded(
  CreateMember cmd,
  CreatedOn on,
  VerificationToken tok,
  MemberID memberID
)
  implements Event {}
