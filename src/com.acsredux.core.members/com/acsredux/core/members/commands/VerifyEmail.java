package com.acsredux.core.members.commands;

import static com.acsredux.core.base.Util.req;

import com.acsredux.core.members.values.VerificationToken;
import java.util.ResourceBundle;
import javax.security.auth.Subject;

/**
 * A VerifyEmail command is submitted when a member clicks the
 * token link we sent in the welcome email.
 */
public record VerifyEmail(Subject subject, VerificationToken token)
  implements BaseMemberCommand {
  public VerifyEmail {
    var rb = ResourceBundle.getBundle("MemberErrorMessages");
    req(subject, "Subject");
    req(token, rb.getString("token_missing"));
  }
}
