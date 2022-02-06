package com.acsredux.core.members.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.CreatedOn;

public record MemberLoggedIn(LoginMember cmd, CreatedOn on, Member member)
  implements Event {}
