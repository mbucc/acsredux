package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.auth.AdminHttpPrincipal;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BaseView {

  public final String pageTitle;
  public final List<Map<String, String>> menuItems;
  public final String error;
  public final long principalID;
  public String principalName;
  public final boolean isLoggedIn;
  public final boolean isAdmin;
  public final boolean isInAlphaTesting;
  public final String suggestionBoxURL;
  public final int alphaTestMemberLimit;

  public BaseView(HttpExchange x1, FormData x2, SiteInfo x3, String title) {
    this.pageTitle = title;

    this.principalID = x2.getPrincipalID();
    this.isAdmin = x1.getPrincipal() instanceof AdminHttpPrincipal;
    this.isLoggedIn = this.isAdmin || (x1.getPrincipal() instanceof MemberHttpPrincipal);
    if (this.isLoggedIn) {
      this.principalName = x1.getPrincipal().getUsername();
    }
    this.error = x2.get("error");

    this.isInAlphaTesting = x3.siteStatus() == SiteStatus.ALPHA;
    this.suggestionBoxURL = x3.suggestionBoxURL().toString();
    this.alphaTestMemberLimit = x3.limitOnAlphaCustomers();
    this.menuItems =
      List.of(
        Map.of("link", "/", "text", "home"),
        Map.of("link", suggestionBoxURL, "text", "suggestions")
      );
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", BaseView.class.getSimpleName() + "[", "]")
      .add("pageTitle='" + pageTitle + "'")
      .add("menuItems=" + menuItems)
      .add("error='" + error + "'")
      .add("principalID=" + principalID)
      .add("principalName=" + principalName)
      .add("isPrincipalAdmin=" + isAdmin)
      .add("isInAlphaTesting=" + isInAlphaTesting)
      .add("suggestionBoxURL='" + suggestionBoxURL + "'")
      .add("alphaTestMemberLimit=" + alphaTestMemberLimit)
      .toString();
  }
}
