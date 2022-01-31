package com.acsredux.core.admin.services;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.*;

public class AdminServiceProvider implements AdminService {

  private final AdminReader reader;

  public AdminServiceProvider(AdminReader r) {
    this.reader = r;
  }

  public SiteInfo getSiteInfo() {
    return reader.getSiteInfo();
  }
}
