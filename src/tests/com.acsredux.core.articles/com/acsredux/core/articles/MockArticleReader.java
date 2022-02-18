package com.acsredux.core.articles;

import static com.acsredux.lib.testutil.TestData.TEST_ARTICLE;

import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;

public class MockArticleReader implements ArticleReader {

  @Override
  public Article getArticle(ArticleID x) {
    return TEST_ARTICLE;
  }
}
