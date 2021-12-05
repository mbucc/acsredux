package com.acsredux.user.services;

import com.acsredux.user.commands.AddUser;
import java.time.Instant;

public record InsertedNewUser(AddUser command, long newUserID, Instant savedOn) {}
