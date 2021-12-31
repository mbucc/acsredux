package com.acsredux.core.admin.services;

import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.ports.Reader;
import com.acsredux.core.admin.values.*;
import java.time.Clock;

public class AdminServiceProvider implements AdminService {

  private final Reader reader;
  private final Clock clock;

  public AdminServiceProvider(Reader r, Clock c) {
    this.reader = r;
    this.clock = c;
  }

  public SiteInfo getSiteInfo() {
    return null;
  }
}
