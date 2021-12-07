package com.acsredux.user.commands;

import com.acsredux.base.Command;
import com.acsredux.base.values.ClearTextPassword;
import com.acsredux.base.values.Email;
import com.acsredux.base.values.FirstName;
import com.acsredux.base.values.GrowingZone;
import com.acsredux.base.values.LastName;
import com.acsredux.base.values.Slug;

public record AddUser(
  FirstName firstName,
  LastName lastName,
  Email email,
  ClearTextPassword password,
  GrowingZone zone,
  Slug slug
)
  implements Command {}