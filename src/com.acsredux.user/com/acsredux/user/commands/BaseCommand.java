package com.acsredux.user.commands;

import com.acsredux.base.Command;

public sealed interface BaseCommand extends Command permits AddUser {}
