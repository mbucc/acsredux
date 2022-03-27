package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.adapter.web.members.Util;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.MemberStatus;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashboardView extends BaseView {

  final long memberID;
  String firstName;
  String lastName;
  String memberSince;
  int memberCount;
  boolean isWaitingOnEmailVerification;
  boolean isEmailVerified;
  List<ArticleView> articles;

  public DashboardView(HttpExchange x1, FormData x2, SiteInfo x3, String title) {
    super(x1, x2, x3, title);
    this.memberID = Util.uriToLong(x1.getRequestURI());
  }

  public boolean isMyPage() {
    return Objects.equals(memberID, principalID);
  }

  public DashboardView lookupMemberInfo(MemberService x) {
    Member m = x.getByID(new MemberID(this.memberID));
    this.firstName = m.firstName().val();
    this.lastName = m.lastName().val();
    this.memberSince = m.memberSince();
    this.memberCount = x.activeMembers();

    if (isMyPage() || m.isAdmin()) {
      this.isWaitingOnEmailVerification =
        m.status() == MemberStatus.NEEDS_EMAIL_VERIFICATION;
      this.isEmailVerified = m.status() == MemberStatus.ACTIVE;
    }

    return this;
  }

  public DashboardView lookupMemberArticles(ContentService x) {
    this.articles =
      x
        .findTopLevelContentByMemberID(new MemberID(this.memberID))
        .stream()
        .map(ArticleView::of)
        .collect(Collectors.toList());
    return this;
  }
}
