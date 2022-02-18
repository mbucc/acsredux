package com.acsredux.core.articles.ports;

import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;

public interface ArticleReader {
  Article getArticle(ArticleID x);
}
