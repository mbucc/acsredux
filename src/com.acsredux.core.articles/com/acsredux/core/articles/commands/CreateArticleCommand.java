package com.acsredux.core.articles.commands;

import com.acsredux.core.articles.values.Article;
import com.acsredux.core.base.Command;
import java.security.Principal;

public record CreateArticleCommand(Principal x1, Article x)
  implements Command, BaseArticleCommand {}
