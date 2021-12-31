package com.acsredux.core.admin;

import com.acsredux.core.admin.ports.Reader;
import com.acsredux.core.admin.services.AdminServiceProvider;
import java.time.Clock;
import java.time.ZoneId;

public final class AdminServiceFactory {

  private AdminServiceFactory() {
    throw new UnsupportedOperationException("static only");
  }

  public static AdminService getAdminService(Reader r, ZoneId tz) {
    return new AdminServiceProvider(r, Clock.system(tz));
  }
}