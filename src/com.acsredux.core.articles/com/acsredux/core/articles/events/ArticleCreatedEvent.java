package com.acsredux.core.articles.events;

import com.acsredux.core.articles.commands.CreatePhotoDiary;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.Event;

public record ArticleCreatedEvent(CreatePhotoDiary cmd, ArticleID id) implements Event {}
