package com.acsredux.lib.testutil;

import static com.acsredux.core.members.services.PasswordUtil.hashpw;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.events.ArticleCreatedEvent;
import com.acsredux.core.articles.values.*;
import com.acsredux.core.members.commands.*;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import java.net.URI;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;

public class TestData {

  public static final Email TEST_EMAIL = new Email("test@example.com");
  public static final FirstName TEST_FIRST_NAME = new FirstName("小川");
  public static final LastName TEST_LAST_NAME = new LastName("治兵衛");

  public static final Email TEST_EMAIL2 = new Email("test2@example.com");
  public static final FirstName TEST_FIRST_NAME2 = new FirstName("Bill");
  public static final LastName TEST_LAST_NAME2 = new LastName("Walton");

  public static final MemberID TEST_MEMBER_ID = new MemberID(123L);
  public static final Principal TEST_PRINCIPAL = new MemberPrincipal(TEST_MEMBER_ID);
  public static final Subject TEST_SUBJECT = new Subject(
    true,
    Set.of(TEST_PRINCIPAL),
    Collections.emptySet(),
    Collections.emptySet()
  );

  public static final ClearTextPassword TEST_CLEAR_TEXT_PASSWORD = ClearTextPassword.of(
    "a3cDefg!"
  );
  public static final VerificationToken TEST_VERIFICATION_TOKEN = new VerificationToken(
    "test token"
  );
  public static final ZipCode TEST_ZIP_CODE = new ZipCode("02134");
  public static final MemberStatus TEST_MEMBER_STATUS = MemberStatus.ACTIVE;
  public static final HashedPassword TEST_HASHED_PASSWORD = hashpw(
    TEST_CLEAR_TEXT_PASSWORD
  );
  public static final RegistrationDate TEST_REGISTRATION_DATE = new RegistrationDate(
    Instant.ofEpochSecond(1642250459)
  );
  public static final ZoneId TEST_TIME_ZONE = ZoneId.of("US/Eastern");
  public static final LoginTime TEST_LOGIN_TIME = new LoginTime(Instant.now());
  public static final LoginTime TEST_SECOND_LOGIN_TIME = null;
  public static final Member TEST_MEMBER = new Member(
    TEST_MEMBER_ID,
    TEST_EMAIL,
    TEST_FIRST_NAME,
    TEST_LAST_NAME,
    TEST_ZIP_CODE,
    TEST_MEMBER_STATUS,
    TEST_HASHED_PASSWORD,
    TEST_REGISTRATION_DATE,
    TEST_TIME_ZONE,
    TEST_LOGIN_TIME,
    TEST_SECOND_LOGIN_TIME
  );
  public static final MemberDashboard TEST_MEMBER_DASHBOARD = new MemberDashboard(
    TEST_MEMBER
  );
  public static final AddMember TEST_ADD_MEMBER_CMD = new AddMember(
    TEST_SUBJECT,
    TEST_FIRST_NAME,
    TEST_LAST_NAME,
    TEST_EMAIL,
    TEST_CLEAR_TEXT_PASSWORD,
    TEST_CLEAR_TEXT_PASSWORD,
    TEST_ZIP_CODE
  );
  public static final AddMember TEST_ADD_MEMBER2_CMD = new AddMember(
    TEST_SUBJECT,
    TEST_FIRST_NAME2,
    TEST_LAST_NAME2,
    TEST_EMAIL2,
    TEST_CLEAR_TEXT_PASSWORD,
    TEST_CLEAR_TEXT_PASSWORD,
    TEST_ZIP_CODE
  );
  public static final VerifyEmail TEST_VERIFY_EMAIL_CMD = new VerifyEmail(
    TEST_SUBJECT,
    TEST_VERIFICATION_TOKEN
  );
  public static final SessionID TEST_SESSION_ID = SessionID.of("Test Session ID");
  public static final LoginMember TEST_LOGIN_MEMBER_CMD = new LoginMember(
    TEST_SUBJECT,
    TEST_EMAIL,
    TEST_CLEAR_TEXT_PASSWORD
  );

  static final URI TEST_SUGGESTION_URI;

  static {
    try {
      TEST_SUGGESTION_URI = new URI("http://example.com/suggestions");
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static final SiteInfo TEST_SITE_INFO = new SiteInfo(
    SiteStatus.ALPHA,
    25, // limitOnAlphaCustomers
    50, // limitOnBetaCustomers,
    0, // currentAnnualSubscriptionFeeInCents,
    "My New Website",
    "An on-line community.",
    "123 Main St",
    "Anyville",
    "MA",
    "12345",
    "mkbucc@gmail.com",
    TEST_SUGGESTION_URI,
    Duration.ofDays(365)
  );

  public static final ArticleID TEST_ARTICLE_ID = new ArticleID(123L);
  public static final Image TEST_IMAGE = Image.of("http://example.com/img1.png");
  public static final Section TEST_SECTION = new Section(List.of(TEST_IMAGE));
  public static final Article TEST_ARTICLE = new Article(
    TEST_MEMBER_ID,
    Title.of("A Test Title"),
    List.of(TEST_SECTION),
    new PublishedDate(Instant.now())
  );

  public static final CreateArticleCommand TEST_CREATE_ARTICLE_COMMAND = new CreateArticleCommand(
    TEST_SUBJECT,
    TEST_ARTICLE
  );
  public static final ArticleCreatedEvent TEST_ARTICLE_CREATED_EVENT = new ArticleCreatedEvent(
    TEST_CREATE_ARTICLE_COMMAND,
    TEST_ARTICLE_ID
  );
}
