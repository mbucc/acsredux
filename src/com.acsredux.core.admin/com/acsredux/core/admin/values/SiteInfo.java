package com.acsredux.core.admin.values;

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
  String fromEmail
) {}
