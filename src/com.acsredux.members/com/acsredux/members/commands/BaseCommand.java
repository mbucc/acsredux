package com.acsredux.members.commands;

import com.acsredux.base.Command;

public sealed interface BaseCommand extends Command permits AddMember {}
