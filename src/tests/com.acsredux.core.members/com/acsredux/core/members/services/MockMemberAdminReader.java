package com.acsredux.core.members.services;

import static com.acsredux.lib.testutil.TestData.TEST_SITE_INFO;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.ports.MemberAdminReader;

public class MockMemberAdminReader implements MemberAdminReader {

  private final SiteInfo siteInfo;

  public MockMemberAdminReader() {
    this(TEST_SITE_INFO);
  }

  public MockMemberAdminReader(SiteInfo x) {
    this.siteInfo = x;
  }

  @Override
  public SiteInfo getSiteInfo() {
    return siteInfo;
  }
}
