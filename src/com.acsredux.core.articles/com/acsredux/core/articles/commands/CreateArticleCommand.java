package com.acsredux.core.articles.commands;

import com.acsredux.core.articles.values.Article;
import java.util.Objects;
import javax.security.auth.Subject;

public final class CreateArticleCommand extends BaseArticleCommand {

  final Article article;

  public CreateArticleCommand(Subject subject, Article article) {
    super(subject);
    Objects.requireNonNull(article);
    this.article = article;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CreateArticleCommand that = (CreateArticleCommand) o;
    return article.equals(that.article);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), article);
  }

  @Override
  public String toString() {
    return "CreateArticleCommand{" + "article=" + article + "} " + super.toString();
  }

  public Article article() {
    return article;
  }
}
