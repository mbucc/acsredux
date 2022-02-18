package com.acsredux.core.articles.ports;

import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.values.ArticleID;
import java.security.Principal;
import java.time.Instant;

public interface ArticleWriter {
  ArticleID createArticle(Principal x1, CreateArticleCommand x2, Instant instant);
}
