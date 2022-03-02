package com.acsredux.adapter.web.views;

import com.acsredux.core.content.entities.Content;

record ArticleView(String title, long id) {
  static ArticleView of(Content x) {
    return new ArticleView(x.title().val(), x.id().val());
  }
}
