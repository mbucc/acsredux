package com.acsredux.core.members.commands;

import static com.acsredux.core.base.Util.die;
import static com.acsredux.core.base.Util.req;

import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.ZipCode;
import java.util.ResourceBundle;
import javax.security.auth.Subject;

public record CreateMember(
  Subject subject,
  FirstName firstName,
  LastName lastName,
  Email email,
  ClearTextPassword password1,
  ClearTextPassword password2,
  ZipCode zipCode
)
  implements BaseMemberCommand {
  public CreateMember {
    var rb = ResourceBundle.getBundle("MemberErrorMessages");
    die(subject, "Subject null");
    req(firstName, rb.getString("firstname_missing"));
    req(lastName, rb.getString("lastname_missing"));
    req(email, rb.getString("email_missing"));
    req(password1, rb.getString("password1_missing"));
    req(password2, rb.getString("password2_missing"));
    req(zipCode, rb.getString("zipcode_missing"));
  }
}
