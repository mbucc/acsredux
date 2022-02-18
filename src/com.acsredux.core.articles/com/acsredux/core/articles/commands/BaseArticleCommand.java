package com.acsredux.core.articles.commands;

import com.acsredux.core.base.BaseCommand;
import java.security.Principal;

public abstract sealed class BaseArticleCommand
  extends BaseCommand
  permits CreateArticleCommand {

  public BaseArticleCommand(Principal principal) {
    super(principal);
  }

  @Override
  public String toString() {
    return "BaseArticleCommand{} " + super.toString();
  }
}
