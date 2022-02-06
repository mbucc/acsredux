package com.acsredux.core.members.commands;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;

public record LoginMember(Email email, ClearTextPassword password)
  implements MemberCommand {}
