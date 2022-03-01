package com.acsredux.core.articles.commands;

import static com.acsredux.core.base.Util.req;

import com.acsredux.core.articles.values.DiaryName;
import com.acsredux.core.articles.values.DiaryYear;
import javax.security.auth.Subject;

public record CreatePhotoDiary(Subject subject, DiaryYear year, DiaryName name)
  implements BaseArticleCommand {
  public CreatePhotoDiary {
    req(subject, "Subject");
    req(year, "Year");
  }
}
