package com.acsredux.core.articles;

import com.acsredux.core.articles.commands.BaseArticleCommand;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public interface ArticleService {
  // Queries
  Article getArticle(ArticleID x);
  List<Article> findArticlesByMemberID(MemberID x);

  // Commands
  List<Event> handle(BaseArticleCommand x);
}
