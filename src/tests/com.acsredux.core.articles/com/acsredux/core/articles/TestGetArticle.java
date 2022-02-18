package com.acsredux.core.articles;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.core.articles.events.ArticleCreatedEvent;
import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.services.ArticleServiceProvider;
import com.acsredux.core.base.Event;
import com.acsredux.lib.testutil.MockProxy;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestGetArticle {

  private final Instant clockTime = Instant.now();

  private ArticleService svc;
  private ArticleReader r;
  private ArticleWriter w;

  @BeforeEach
  void setup() {
    r = (ArticleReader) MockProxy.of(new MockArticleReader());
    w = (ArticleWriter) MockProxy.of(new MockArticleWriter());
    InstantSource c = InstantSource.fixed(this.clockTime);
    this.svc = new ArticleServiceProvider(r, w, c);
  }

  @Test
  void testGetArticle() {
    // execute
    var y = svc.getArticle(TEST_ARTICLE_ID);

    // verify
    assertEquals(TEST_ARTICLE, y);
    MockProxy.toProxy(r).assertCallCount(1).assertCall(0, "getArticle", TEST_ARTICLE_ID);
  }

  {
    r = (ArticleReader) MockProxy.of(new MockArticleReader());
    w = (ArticleWriter) MockProxy.of(new MockArticleWriter());
    this.svc = new ArticleServiceProvider(r, w, null);
  }

  @Test
  void testCreateArticle() {
    // execute
    List<Event> ys = svc.handle(TEST_CREATE_ARTICLE_COMMAND);

    // verify
    assertEquals(1, ys.size());
    if (ys.get(0) instanceof ArticleCreatedEvent y1) {
      assertEquals(TEST_ARTICLE_CREATED_EVENT, y1);
    } else {
      fail("Wrong event type returned by create: " + ys.get(0));
    }
    MockProxy
      .toProxy(w)
      .assertCallCount(1)
      .assertCall(0, "createArticle", TEST_CREATE_ARTICLE_COMMAND);
  }
}
