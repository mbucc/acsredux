package com.acsredux.adapter.filesystem;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import java.net.URI;
import java.time.Duration;
import java.util.StringJoiner;

public class SiteInfoDTO {

  String siteTitle;
  String siteDescription;
  String siteEmail;
  String analyticsScriptTag;

  SiteInfoDTO() {}

  public SiteInfo asSiteInfo() {
    try {
      String x =
        "<script data-goatcounter='https://gardendiary.goatcounter.com/count' async src='//gc.zgo.at/count.js'></script>";
      return new SiteInfo(
        SiteStatus.ALPHA,
        25,
        100,
        0,
        siteTitle,
        siteDescription,
        "",
        "",
        "",
        "",
        siteEmail,
        new URI("http://example.com"),
        Duration.ofDays(365),
        analyticsScriptTag
      );
    } catch (Exception e) {
      throw new IllegalStateException("illegal siteinfo json: " + this, e);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SiteInfoDTO.class.getSimpleName() + "[", "]")
      .add("siteTitle='" + siteTitle + "'")
      .add("siteDescription='" + siteDescription + "'")
      .add("siteEmail='" + siteEmail + "'")
      .toString();
  }
}
