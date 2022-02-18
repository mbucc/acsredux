package com.acsredux.core.articles.events;

import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;

public record ArticleCreatedEvent(CreateArticleCommand cmd, ArticleID id)
  implements Event {}
