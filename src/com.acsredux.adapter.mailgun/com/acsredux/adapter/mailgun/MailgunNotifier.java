package com.acsredux.adapter.mailgun;

import static java.net.http.HttpRequest.BodyPublishers;
import static java.net.http.HttpResponse.BodyHandlers;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.Notifier;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.BiConsumer;

public final class MailgunNotifier implements Notifier {

  private final Configuration conf;
  private final HttpClient http;

  public MailgunNotifier() {
    this(Configuration.loadFromEnvironment());
  }

  MailgunNotifier(Configuration conf) {
    this.conf = conf;
    this.http =
      HttpClient
        .newBuilder()
        .authenticator(new BasicAuth(conf))
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(conf.timeout()))
        .build();
  }

  String emailBody(MemberAdded event) {
    String fmt = "Click %s/%s to verify your email and activate your membership.";
    return String.format(fmt, conf.activationURL(), event.tok().val());
  }

  static String encode(String x) {
    return URLEncoder.encode(x, StandardCharsets.UTF_8);
  }

  String postBody(MemberAdded event, SiteInfo siteInfo) {
    StringBuilder buf = new StringBuilder();
    BiConsumer<String, String> addToBuf = (k, v) -> {
      buf.append(encode(k));
      buf.append("=");
      buf.append(encode(v));
      buf.append("&");
    };
    String[][] pairs = {
      { "from", siteInfo.fromEmail() },
      { "to", event.cmd().email().val() },
      { "subject", "Welcome!" },
      { "text", emailBody(event) },
    };
    for (String[] pair : pairs) {
      addToBuf.accept(pair[0], pair[1]);
    }
    buf.deleteCharAt(buf.length() - 1);
    return buf.toString();
  }

  @Override
  public void memberAdded(MemberAdded event, SiteInfo siteInfo) {
    final HttpResponse<String> response;
    final String postBody = postBody(event, siteInfo);
    try {
      var request = HttpRequest
        .newBuilder()
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(BodyPublishers.ofString(postBody))
        .uri(conf.uri())
        .build();
      response = http.send(request, BodyHandlers.ofString());
    } catch (Exception e) {
      throw new IllegalStateException("Activation email failed for " + event, e);
    }
    System.out.println(response.statusCode());
    if (response.statusCode() != 200) {
      System.out.println("request:\n" + postBody);
      String responseBody = response.body();
      System.out.println("response:\n" + responseBody);
      throw new IllegalStateException("Activation email failed to send: " + responseBody);
    }
  }
}
