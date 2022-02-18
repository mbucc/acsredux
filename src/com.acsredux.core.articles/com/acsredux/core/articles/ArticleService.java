package com.acsredux.core.articles;

import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

public interface ArticleService {
  Article getArticle(ArticleID x);
  List<Event> createArticle(Principal x1, CreateArticleCommand x2, Instant now);
}
