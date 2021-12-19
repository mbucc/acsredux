package com.acsredux.members.events;

import com.acsredux.base.Event;
import com.acsredux.members.commands.AddMember;
import com.acsredux.members.values.CreatedOn;
import com.acsredux.members.values.MemberID;
import com.acsredux.members.values.VerificationToken;

public record MemberAdded(
  AddMember cmd,
  CreatedOn on,
  VerificationToken tok,
  MemberID memberID
)
  implements Event {}
