package com.acsredux.lib.testutil;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import java.time.Instant;
import java.time.ZoneId;

public class TestData {

  public static final Email TEST_EMAIL = new Email("test@example.com");
  public static final FirstName TEST_FIRST_NAME = new FirstName("小川");
  public static final LastName TEST_LAST_NAME = new LastName("治兵衛");
  public static final ClearTextPassword TEST_PASSWORD = new ClearTextPassword("a3cDefg!");
  public static final VerificationToken TEST_TOKEN = new VerificationToken("test token");
  public static final ZipCode TEST_ZIP_CODE = new ZipCode("02134");
  public static final MemberID TEST_MEMBER_ID = new MemberID(123L);
  public static final MemberStatus TEST_MEMBER_STATUS = MemberStatus.AUTHORIZED;
  public static final EncryptedPassword TEST_ENCRYPTED_PASSWORD = new EncryptedPassword(
    "abc"
  );
  public static final RegistrationDate TEST_REGISTRATION_DATE = new RegistrationDate(
    Instant.ofEpochSecond(1642250459)
  );
  public static final ZoneId TEST_TIME_ZONE = ZoneId.of("US/Eastern");
  public static final Member TEST_MEMBER = new Member(
    TEST_MEMBER_ID,
    TEST_EMAIL,
    TEST_FIRST_NAME,
    TEST_LAST_NAME,
    TEST_ZIP_CODE,
    TEST_MEMBER_STATUS,
    TEST_ENCRYPTED_PASSWORD,
    TEST_REGISTRATION_DATE,
    TEST_TIME_ZONE
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
