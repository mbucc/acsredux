package com.acsredux.lib.testutil;

import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.values.SiteInfo;

public class MockAdminService implements AdminService {

  private SiteInfo siteInfo = TEST_SITE_INFO;

  @Override
  public SiteInfo getSiteInfo() {
    return this.siteInfo;
  }

  public void setSiteInfo(SiteInfo x) {
    this.siteInfo = x;
  }
}
