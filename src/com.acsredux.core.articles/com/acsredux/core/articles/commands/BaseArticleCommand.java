package com.acsredux.core.articles.commands;

import com.acsredux.core.base.BaseCommand;
import javax.security.auth.Subject;

public abstract sealed class BaseArticleCommand
  extends BaseCommand
  permits CreateArticleCommand {

  public BaseArticleCommand(Subject subject) {
    super(subject);
  }
}
