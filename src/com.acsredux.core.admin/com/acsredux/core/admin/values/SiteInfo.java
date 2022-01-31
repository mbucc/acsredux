package com.acsredux.core.admin.values;

import java.net.URI;

public record SiteInfo(
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
  URI suggestionBoxURL
) {}
