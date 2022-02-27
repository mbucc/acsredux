package com.acsredux.core.articles;

import static com.acsredux.lib.testutil.TestData.TEST_ARTICLE;

import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public class MockArticleReader implements ArticleReader {

  @Override
  public Article getArticle(ArticleID x) {
    return TEST_ARTICLE;
  }

  @Override
  public List<Article> findArticlesByMemberID(MemberID x) {
    return List.of(TEST_ARTICLE);
  }
}
