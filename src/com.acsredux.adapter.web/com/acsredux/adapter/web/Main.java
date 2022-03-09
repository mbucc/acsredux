package com.acsredux.adapter.web;

import static java.lang.System.Logger.Level.INFO;

import com.acsredux.adapter.stub.Stub;
import com.acsredux.adapter.web.auth.CookieAuthenticator;
import com.acsredux.adapter.web.members.MembersHandler;
import com.acsredux.adapter.web.photodiary.PhotoDiaryHandler;
import com.acsredux.adapter.web.staticfiles.StaticFileHandler;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.AdminServiceFactory;
import com.acsredux.core.auth.SecurityPolicy;
import com.acsredux.core.auth.SecurityPolicyProvider;
import com.acsredux.core.auth.SecurityProxy;
import com.acsredux.core.base.Util;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.ContentServiceFactory;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.MemberServiceFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.ZoneId;
import java.util.concurrent.Executors;

public class Main {

  private static final System.Logger LOGGER = System.getLogger("ConsoleLogger");

  private Main() {
    throw new UnsupportedOperationException("static only");
  }

  public static void main(String[] args) throws IOException {
    var xs = CommandLineArguments.scan(args);
    LOGGER.log(INFO, "creating HttpServer on port {0}", xs.portAsString());

    //
    //			Load services
    //
    Stub stub = new Stub();
    stub.setDocumentRoot(xs.documentRoot);

    SecurityPolicy policy = SecurityPolicyProvider.parse(
      Util.readResource("security-policy.json")
    );

    ZoneId tz = ZoneId.of("US/Eastern");
    MemberService.passwordSaltOrDie();

    MemberService memberService = SecurityProxy.of(
      MemberServiceFactory.getMemberService(stub, stub, stub, stub, tz),
      policy
    );
    AdminService adminService = SecurityProxy.of(
      AdminServiceFactory.getAdminService(stub, tz),
      policy
    );
    ContentService contentService = SecurityProxy.of(
      ContentServiceFactory.getArticleService(tz, stub, stub, stub),
      policy
    );

    //
    //			Create server
    //
    HttpServer server = HttpServer.create(
      new InetSocketAddress(xs.host, xs.port),
      xs.backlog
    );
    server.setExecutor(Executors.newFixedThreadPool(xs.threads));

    //
    //			Setup path handlers.
    //
    record Pair(String path, HttpHandler handler) {}
    Pair[] pairs = new Pair[] {
      new Pair("/", new RootHandler(adminService, xs.documentRoot)),
      new Pair(
        "/members",
        new MembersHandler(xs.documentRoot, memberService, adminService, contentService)
      ),
      new Pair(
        "/photo-diary",
        new PhotoDiaryHandler(xs.documentRoot, contentService, adminService)
      ),
      new Pair("/static", new StaticFileHandler("/static", xs.documentRoot + "/static")),
    };
    CookieAuthenticator auth = new CookieAuthenticator(memberService);
    for (Pair p : pairs) {
      var ctx = server.createContext(p.path, p.handler);
      ctx.setAuthenticator(auth);
    }

    //
    //			Start server
    //
    server.start();
  }
}
