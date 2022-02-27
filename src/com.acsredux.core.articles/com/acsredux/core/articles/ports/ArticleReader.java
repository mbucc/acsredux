package com.acsredux.core.articles.ports;

import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public interface ArticleReader {
  Article getArticle(ArticleID x);
  List<Article> findArticlesByMemberID(MemberID x);
}
