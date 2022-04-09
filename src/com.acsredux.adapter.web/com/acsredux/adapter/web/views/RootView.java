package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.sun.net.httpserver.HttpExchange;
import java.util.StringJoiner;

public class RootView extends BaseView {

  public final String siteDescription;

  public RootView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3, x3.name());
    this.siteDescription = x3.description();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", RootView.class.getSimpleName() + "[", "]")
      .add("siteDescription='" + siteDescription + "'")
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
