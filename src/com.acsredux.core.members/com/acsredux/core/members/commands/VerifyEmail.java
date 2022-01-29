package com.acsredux.core.members.commands;

import com.acsredux.core.members.values.VerificationToken;

/**
 * A VerifyEmail command is submitted when a member clicks the
 * token link we sent in the welcome email.
 */
public record VerifyEmail(VerificationToken token) implements MemberCommand {}
