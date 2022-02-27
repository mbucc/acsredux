package com.acsredux.lib.testutil;

import static com.acsredux.lib.testutil.TestData.TEST_ARTICLE;

import com.acsredux.core.articles.ArticleService;
import com.acsredux.core.articles.commands.BaseArticleCommand;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;
import com.acsredux.core.members.values.MemberID;
import java.util.Collections;
import java.util.List;

public class MockArticleService implements ArticleService {

  @Override
  public Article getArticle(ArticleID x) {
    return TEST_ARTICLE;
  }

  @Override
  public List<Article> findArticlesByMemberID(MemberID x) {
    return List.of(TEST_ARTICLE);
  }

  @Override
  public List<Event> handle(BaseArticleCommand x) {
    return Collections.emptyList();
  }
}
