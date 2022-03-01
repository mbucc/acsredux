package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.sun.net.httpserver.HttpExchange;
import java.util.StringJoiner;

public class CreateArticleView extends BaseView {

  final String year;
  final String name;

  public CreateArticleView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3, "Create Article");
    this.year = x2.get("year");
    this.name = x2.get("name");
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CreateArticleView.class.getSimpleName() + "[", "]")
      .add("pageTitle='" + pageTitle + "'")
      .add("menuItems=" + menuItems)
      .add("error='" + error + "'")
      .add("principalID=" + principalID)
      .add("principalName='" + principalName + "'")
      .add("isLoggedIn=" + isLoggedIn)
      .add("isAdmin=" + isAdmin)
      .add("isInAlphaTesting=" + isInAlphaTesting)
      .add("suggestionBoxURL='" + suggestionBoxURL + "'")
      .add("alphaTestMemberLimit=" + alphaTestMemberLimit)
      .toString();
  }
}
