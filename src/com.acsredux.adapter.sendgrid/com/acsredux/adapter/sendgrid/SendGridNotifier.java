package com.acsredux.adapter.sendgrid;

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

public final class SendGridNotifier implements Notifier {

  private final Configuration conf;
  private final HttpClient http;

  public SendGridNotifier() {
    this(Configuration.loadFromEnvironment());
  }

  SendGridNotifier(Configuration conf) {
    this.conf = conf;
    this.http =
      HttpClient
        .newBuilder()
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(conf.timeout()))
        .build();
  }

  static String encode(String x) {
    return URLEncoder.encode(x, StandardCharsets.UTF_8);
  }

  String postBody(MemberAdded event, SiteInfo siteInfo) {
    var fmt =
      """
        {
           "from":{
              "email":"%s"
           },
           "personalizations":[
              {
                 "to":[
                    {
                       "email":"%s"
                    }
                 ],
                 "dynamic_template_data":{
                    "activation_url": "http://example.com/activate",
                    "dashboard_url": "http://example.com/activate",
                    "billing_url": "http://example.com/activate",
                    "help_url": "http://example.com/activate",
                    "from_name": "%s",
                    "name": "%s",
                    "street": "%s",
                    "city": "%s",
                    "state": "%s",
                    "zip": "%s"
                 }
              }
           ],
           "template_id":"%s"
        }""";
    return String.format(
      fmt,
      siteInfo.fromEmail(),
      event.cmd().email().val(),
      siteInfo.name(),
      siteInfo.name(),
      siteInfo.streetAddress(),
      siteInfo.city(),
      siteInfo.stateAbbreviation(),
      siteInfo.zip(),
      this.conf.welcomeTemplateId()
    );
  }

  @Override
  public void memberAdded(MemberAdded event, SiteInfo siteInfo) {
    final HttpResponse<String> response;
    try {
      var request = HttpRequest
        .newBuilder()
        .header("Authorization", "Bearer " + new String(conf.apiKey()))
        .header("Content-Type", "application/json")
        .timeout(Duration.ofSeconds(conf.timeout()))
        .POST(BodyPublishers.ofString(encode(postBody(event, siteInfo))))
        .uri(conf.apiURI())
        .build();
      response = http.send(request, BodyHandlers.ofString());
    } catch (Exception e) {
      throw new IllegalStateException("Activation email failed for " + event, e);
    }
    System.out.println(response.statusCode());
    if (response.statusCode() != 202) {
      System.out.println("request:\n" + postBody(event, siteInfo));
      System.out.println("response:\n" + response.body());
      throw new IllegalStateException("Activation email failed to send");
    }
  }
}
