package com.acsredux.core.content.commands;

import static com.acsredux.core.base.Util.die;
import static com.acsredux.core.base.Util.req;

import com.acsredux.core.content.values.DiaryName;
import com.acsredux.core.content.values.DiaryYear;
import java.util.ResourceBundle;
import javax.security.auth.Subject;

public record CreatePhotoDiary(Subject subject, DiaryYear year, DiaryName name)
  implements BaseContentCommand {
  public CreatePhotoDiary {
    var rb = ResourceBundle.getBundle("ContentErrorMessages");
    die(subject, "null Subject");
    req(year, rb.getString("year_missing"));
  }
}
