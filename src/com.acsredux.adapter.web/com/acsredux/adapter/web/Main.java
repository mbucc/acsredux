package com.acsredux.adapter.web;

import static com.acsredux.lib.env.VariableUtil.getString;
import static java.lang.System.Logger.Level.INFO;

import com.acsredux.adapter.filesystem.FileSystem;
import com.acsredux.adapter.stub.Stub;
import com.acsredux.adapter.web.auth.CookieAuthenticator;
import com.acsredux.adapter.web.content.ContentHandler;
import com.acsredux.adapter.web.members.MembersHandler;
import com.acsredux.adapter.web.photodiary.PhotoDiaryHandler;
import com.acsredux.adapter.web.staticfiles.StaticFileHandler;
import com.acsredux.core.admin.AdminService;
import com.acsredux.core.admin.AdminServiceFactory;
import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.auth.SecurityPolicy;
import com.acsredux.core.auth.SecurityPolicyProvider;
import com.acsredux.core.auth.SecurityProxy;
import com.acsredux.core.base.Util;
import com.acsredux.core.content.ContentService;
import com.acsredux.core.content.ContentServiceFactory;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageReader;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.MemberServiceFactory;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.lib.env.Variable;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.ZoneId;
import java.util.concurrent.Executors;

/**
 * Start webserver.   Command-line options are:
 * <ul>
 *   <li>-h &lt;host> (default=localhost)
 *   <li>-p &lt;port> (default=8000)
 *   <li>-r &lt;document_root>
 *   <li>-b &lt;backlog> (default=0)
 *   <li>-t &lt;threads> (default=10)
 * </ul>
 */
public class Main {

  private static final System.Logger LOGGER = System.getLogger("ConsoleLogger");

  private Main() {
    throw new UnsupportedOperationException("static only");
  }

  public static void main(String[] args) throws IOException {
    var xs = CommandLineArguments.scan(args);
    LOGGER.log(INFO, "creating HttpServer on port {0}", xs.portAsString());

    //
    //			Load adapters
    //

    Stub stub = new Stub();
    stub.setDocumentRoot(xs.documentRoot);

    // TODO: Use ServiceProvider in factories.
    MemberNotifier mn = stub;
    MemberReader mr = stub;
    MemberWriter mw = stub;
    MemberAdminReader mar = stub;
    AdminReader ar = stub;
    ContentReader cr = stub;
    ContentWriter cw = stub;
    ImageReader ir = stub;
    ImageWriter iw = stub;

    if ("Y".equals(getString(Variable.IS_PRODUCTION, System.getenv()))) {
      System.out.println("using production adapters.");
      //mn = new MailgunNotifier();
      FileSystem fs = new FileSystem();
      fs.setDocumentRoot(xs.documentRoot);
      mr = fs;
      mw = fs;
      mar = fs;
      ar = fs;
      cr = fs;
      cw = fs;
    }

    //
    //			Load services
    //

    SecurityPolicy policy = SecurityPolicyProvider.parse(
      Util.readResource("security-policy.json")
    );

    ZoneId tz = ZoneId.of("US/Eastern");
    MemberService.passwordSaltOrDie();

    MemberService memberService = SecurityProxy.of(
      MemberServiceFactory.getMemberService(mr, mw, mn, mar, tz),
      policy
    );
    AdminService adminService = SecurityProxy.of(
      AdminServiceFactory.getAdminService(ar, tz),
      policy
    );
    ContentService contentService = SecurityProxy.of(
      ContentServiceFactory.getArticleService(tz, cr, cw, ir, iw),
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
    //			Create context for each service and add the CookieAuthenticator to each.
    //

    String templateRoot = xs.documentRoot + "/template";
    record Pair(String path, HttpHandler handler) {}
    Pair[] pairs = new Pair[] {
      new Pair("/", new RootHandler(templateRoot, adminService)),
      new Pair(
        "/members",
        new MembersHandler(templateRoot, memberService, adminService, contentService)
      ),
      new Pair(
        "/photo-diary",
        new PhotoDiaryHandler(templateRoot, contentService, adminService, memberService)
      ),
      new Pair("/static", new StaticFileHandler("/static", xs.documentRoot + "/static")),
      new Pair("/images", new ContentHandler(contentService)),
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
