package com.acsredux.core.admin.values;

import java.net.URI;
import java.time.Duration;

public record SiteInfo(
  SiteStatus siteStatus,
  int limitOnAlphaCustomers,
  int limitOnBetaCustomers,
  int currentAnnualSubscriptionFeeInCents,
  String name,
  String description,
  String streetAddress,
  String city,
  String stateAbbreviation,
  String zip,
  String fromEmail,
  URI suggestionBoxURL,
  Duration cookieMaxAge
) {}
