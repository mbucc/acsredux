package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.MemberService;
import com.sun.net.httpserver.HttpExchange;

public class CreateMemberView extends BaseView {

  int memberCount;
  final String email;
  final String firstName;
  final String lastName;
  final String zip;
  final String pwd1;
  final String pwd2;

  public CreateMemberView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3, "Create Membership");
    this.email = x2.get("email");
    this.firstName = x2.get("firstName");
    this.lastName = x2.get("lastName");
    this.zip = x2.get("zip");
    this.pwd1 = x2.get("pwd1");
    this.pwd2 = x2.get("pwd2");
  }

  public CreateMemberView lookupMemberCount(MemberService x) {
    this.memberCount = x.activeMembers();
    return this;
  }
}
