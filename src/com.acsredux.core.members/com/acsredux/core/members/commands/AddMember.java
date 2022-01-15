package com.acsredux.core.members.commands;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.ZipCode;

public record AddMember(
  FirstName firstName,
  LastName lastName,
  Email email,
  ClearTextPassword password1,
  ClearTextPassword password2,
  ZipCode zipCode
)
  implements MemberCommand {}
