package com.acsredux.adapter.web.auth;

import com.acsredux.core.base.MemberID;
import com.acsredux.core.members.values.AnonymousPrincipal;
import com.acsredux.core.members.values.MemberPrincipal;
import java.security.Principal;

public sealed interface ACSHttpPrincipal
  extends Principal
  permits AnonymousHttpPrincipal, MemberHttpPrincipal, AdminHttpPrincipal {
  default Principal asMemberPrincipal() {
    return switch (this) {
      case AnonymousHttpPrincipal o -> new AnonymousPrincipal();
      case AdminHttpPrincipal o -> new MemberPrincipal(o.getMember().id());
      case MemberHttpPrincipal o -> new MemberPrincipal(o.getMember().id());
    };
  }

  default MemberID memberID() {
    return switch (this) {
      case AnonymousHttpPrincipal o -> null;
      case AdminHttpPrincipal o -> o.getMember().id();
      case MemberHttpPrincipal o -> o.getMember().id();
    };
  }

  static ACSHttpPrincipal of(Principal x) {
    if (x instanceof ACSHttpPrincipal) {
      return (ACSHttpPrincipal) x;
    }
    String fmt = "Principal %s is not an ACSHttpPrincipal";
    throw new IllegalStateException(String.format(fmt, x));
  }
}
