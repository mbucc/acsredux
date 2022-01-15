package com.acsredux.core.members.ports;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.events.MemberAdded;

public interface MemberNotifier {
  void memberAdded(MemberAdded event, SiteInfo siteInfo);
}
