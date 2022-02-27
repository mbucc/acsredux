package com.acsredux.adapter.web.views;

import com.acsredux.adapter.web.common.FormData;
import com.acsredux.core.admin.values.SiteInfo;
import com.sun.net.httpserver.HttpExchange;

public class LoginView extends BaseView {

  public LoginView(HttpExchange x1, FormData x2, SiteInfo x3) {
    super(x1, x2, x3, "Login");
  }

  public String getPageTitle() {
    return this.pageTitle;
  }

  @Override
  public String toString() {
    return "LoginView[" + super.toString() + "]";
  }
}
