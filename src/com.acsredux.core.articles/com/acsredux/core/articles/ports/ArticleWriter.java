package com.acsredux.core.articles.ports;

import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.values.ArticleID;

public interface ArticleWriter {
  ArticleID createArticle(CreateArticleCommand x);
}
