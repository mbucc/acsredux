package com.acsredux.core.articles;

import static com.acsredux.lib.testutil.TestData.TEST_ARTICLE_ID;

import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.values.ArticleID;
import java.security.Principal;
import java.time.Instant;

public class MockArticleWriter implements ArticleWriter {

  @Override
  public ArticleID createArticle(Principal x1, CreateArticleCommand x2, Instant instant) {
    return TEST_ARTICLE_ID;
  }
}
