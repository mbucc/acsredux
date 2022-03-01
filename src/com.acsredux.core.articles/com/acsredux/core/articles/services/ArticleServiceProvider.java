package com.acsredux.core.articles.services;

import com.acsredux.core.articles.ArticleService;
import com.acsredux.core.articles.commands.BaseArticleCommand;
import com.acsredux.core.articles.commands.CreatePhotoDiary;
import com.acsredux.core.articles.events.ArticleCreatedEvent;
import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;
import com.acsredux.core.members.values.MemberID;
import java.time.InstantSource;
import java.util.List;

public class ArticleServiceProvider implements ArticleService {

  final ArticleReader reader;
  final ArticleWriter writer;

  public ArticleServiceProvider(ArticleReader r, ArticleWriter w, InstantSource c) {
    this.reader = r;
    this.writer = w;
  }

  @Override
  public Article getArticle(ArticleID x) {
    return reader.getArticle(x);
  }

  @Override
  public List<Article> findArticlesByMemberID(MemberID x) {
    return reader.findArticlesByMemberID(x);
  }

  @Override
  public List<Event> handle(BaseArticleCommand x) {
    if (x instanceof CreatePhotoDiary x1) {
      return handle(x1);
    } else {
      throw new IllegalStateException("invalid command " + x);
    }
  }

  private List<Event> handle(CreatePhotoDiary x) {
    ArticleID y = writer.createArticle(x);
    return List.of(new ArticleCreatedEvent(x, y));
  }
}
