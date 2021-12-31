package com.acsredux.core.members.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.VerificationToken;

public record MemberAdded(
  AddMember cmd,
  CreatedOn on,
  VerificationToken tok,
  MemberID memberID
)
  implements Event {}
