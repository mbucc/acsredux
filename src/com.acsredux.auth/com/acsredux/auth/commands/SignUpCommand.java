package com.acsredux.auth.commands;

import com.acsredux.base.Command;

public record SignUpCommand(
  String firstName,
  String lastName,
  String email,
  String password,
  String zone,
  String slug
)
  implements Command {}
