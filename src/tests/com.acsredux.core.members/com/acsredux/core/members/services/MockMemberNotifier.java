package com.acsredux.core.members.services;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberNotifier;

public class MockMemberNotifier implements MemberNotifier {

  @Override
  public void memberAdded(MemberAdded event, SiteInfo siteInfo) {}
}
