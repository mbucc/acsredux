package com.acsredux.core.articles;

import static com.acsredux.lib.testutil.TestData.TEST_ARTICLE_ID;

import com.acsredux.core.articles.commands.CreatePhotoDiary;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.values.ArticleID;

public class MockArticleWriter implements ArticleWriter {

  @Override
  public ArticleID createArticle(CreatePhotoDiary x) {
    return TEST_ARTICLE_ID;
  }
}
