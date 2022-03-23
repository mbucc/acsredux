package com.acsredux.core.members.commands;

import static com.acsredux.core.base.Util.req;

import com.acsredux.core.base.Subject;
import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;
import java.util.ResourceBundle;

public record LoginMember(Subject subject, Email email, ClearTextPassword password)
  implements BaseMemberCommand {
  public LoginMember {
    var rb = ResourceBundle.getBundle("MemberErrorMessages");
    req(subject, "Subject");
    req(email, rb.getString("email_missing"));
    req(email, rb.getString("password1_missing"));
  }
}
