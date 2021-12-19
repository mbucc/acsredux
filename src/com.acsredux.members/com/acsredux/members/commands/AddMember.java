package com.acsredux.members.commands;

import com.acsredux.members.values.ClearTextPassword;
import com.acsredux.members.values.Email;
import com.acsredux.members.values.FirstName;
import com.acsredux.members.values.GrowingZone;
import com.acsredux.members.values.LastName;

public record AddMember(
  FirstName firstName,
  LastName lastName,
  Email email,
  ClearTextPassword password1,
  ClearTextPassword password2,
  GrowingZone zone
)
  implements BaseCommand {}
