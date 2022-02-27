package com.acsredux.core.articles;

import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.services.ArticleServiceProvider;
import java.time.Clock;
import java.time.ZoneId;

public class ArticleServiceFactory {

  private ArticleServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static ArticleService getArticleService(
    ArticleReader r,
    ArticleWriter w,
    ZoneId tz
  ) {
    return new ArticleServiceProvider(r, w, Clock.system(tz));
  }
}
