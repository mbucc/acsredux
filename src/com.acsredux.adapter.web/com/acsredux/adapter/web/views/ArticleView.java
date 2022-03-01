package com.acsredux.adapter.web.views;

import com.acsredux.core.articles.values.Article;

record ArticleView(String title, long id) {
  static ArticleView of(Article x) {
    return new ArticleView(x.title().val(), x.id().val());
  }
}
