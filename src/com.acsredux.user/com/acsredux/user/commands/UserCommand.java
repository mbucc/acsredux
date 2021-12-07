package com.acsredux.user.commands;

import com.acsredux.base.Command;

public sealed interface UserCommand extends Command permits AddUser {}
