package com.acsredux.core.admin.services;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;

class MockAdminReader implements AdminReader {

  private SiteInfo siteInfo;

  MockAdminReader(SiteInfo x) {
    this.siteInfo = x;
  }

  @Override
  public SiteInfo getSiteInfo() {
    return this.siteInfo;
  }
}
