package com.acsredux.lib.testutil;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;

public class TestData {

  public static final Email TEST_EMAIL = new Email("test@example.com");
  public static final FirstName TEST_FIRST_NAME = new FirstName("first");
  public static final LastName TEST_LAST_NAME = new LastName("last");
  public static final ClearTextPassword TEST_PASSWORD = new ClearTextPassword("a3cDefg!");
  public static final VerificationToken TEST_TOKEN = new VerificationToken("test token");
  public static final ZipCode TEST_ZIP_CODE = new ZipCode("02134");
  public static final MemberID TEST_MEMBER_ID = new MemberID(123L);
  public static final Member TEST_MEMBER = new Member(
    TEST_MEMBER_ID,
    TEST_EMAIL,
    TEST_FIRST_NAME,
    TEST_LAST_NAME,
    TEST_ZIP_CODE
  );
  public static final AddMember TEST_ADD_MEMBER_CMD = new AddMember(
    TEST_FIRST_NAME,
    TEST_LAST_NAME,
    TEST_EMAIL,
    TEST_PASSWORD,
    TEST_PASSWORD,
    TEST_ZIP_CODE
  );
  public static final SiteInfo TEST_SITEINFO = new SiteInfo(
    25, // limitOnAlphaCustomers
    50, // limitOnBetaCustomers,
    0, // currentAnnualSubscriptionFeeInCents,
    "My New Website",
    "An on-line community.",
    "123 Main St",
    "Anyville",
    "MA",
    "12345",
    "mkbucc@gmail.com"
  );
}
