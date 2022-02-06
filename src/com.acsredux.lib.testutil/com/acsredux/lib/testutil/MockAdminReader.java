package com.acsredux.lib.testutil;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;

public class MockAdminReader implements AdminReader {

  private final SiteInfo siteInfo;

  public MockAdminReader(SiteInfo x) {
    this.siteInfo = x;
  }

  @Override
  public SiteInfo getSiteInfo() {
    return this.siteInfo;
  }
}
