package com.acsredux.core.articles.services;

import com.acsredux.core.articles.ArticleService;
import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.events.ArticleCreatedEvent;
import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;
import java.security.Principal;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;

public class ArticleServiceProvider implements ArticleService {

  final ArticleReader reader;
  final ArticleWriter writer;
  private final InstantSource clock;

  public ArticleServiceProvider(ArticleReader r, ArticleWriter w, InstantSource c) {
    this.reader = r;
    this.writer = w;
    this.clock = c;
  }

  @Override
  public Article getArticle(ArticleID x) {
    return reader.getArticle(x);
  }

  @Override
  public List<Event> createArticle(Principal x1, CreateArticleCommand x2, Instant now) {
    ArticleID y = writer.createArticle(x1, x2, this.clock.instant());
    return List.of(new ArticleCreatedEvent(x2, y));
  }
}
