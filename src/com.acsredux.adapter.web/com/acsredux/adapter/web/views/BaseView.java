package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.auth.AdminHttpPrincipal;
import com.acsredux.adapter.web.auth.MemberHttpPrincipal;
import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class BaseView {

  public record MenuItem(String label, String href) {}

  public final List<MenuItem> menuItems;

  public String pageTitle;
  public final String error;
  public final long principalID;
  public String principalName;
  public final boolean isLoggedIn;
  public final boolean isAdmin;

  // site-wide information.
  public final boolean isInAlphaTesting;
  public final String suggestionBoxURL;
  public final int alphaTestMemberLimit;
  public final String analyticsScriptTag;

  public BaseView(HttpExchange x1, FormData x2, SiteInfo x3) {
    this(x1, x2, x3, "");
  }

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
    this.analyticsScriptTag = x3.analyticsScriptTag();

    this.menuItems = makeMenu(x1);
  }

  private List<MenuItem> makeMenu(HttpExchange x1) {
    var ys = new ArrayList<MenuItem>(2);
    if (this.isLoggedIn) {
      var p = (MemberHttpPrincipal) x1.getPrincipal();
      ys.add(new MenuItem("dashboard", "/members/" + p.getID().val()));
    }
    ys.add(new MenuItem("home", "/"));
    return ys;
  }

  public void setPageTitle(String title) {
    this.pageTitle = title;
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
      .add("analyticsScriptTag='" + analyticsScriptTag + "'")
      .toString();
  }
}
