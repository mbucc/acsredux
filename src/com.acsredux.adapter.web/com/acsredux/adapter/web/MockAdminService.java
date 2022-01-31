package com.acsredux.adapter.web;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;

class MockAdminService implements AdminService {

  private SiteInfo siteInfo;

  @Override
  public SiteInfo getSiteInfo() {
    return this.siteInfo;
  }

  void setSiteInfo(SiteInfo x) {
    this.siteInfo = x;
  }
}
