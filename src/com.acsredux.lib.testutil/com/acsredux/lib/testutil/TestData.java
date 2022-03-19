package com.acsredux.lib.testutil;

import static com.acsredux.core.members.PasswordUtil.hashpw;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.PhotoDiaryCreated;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.ClearTextPassword;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.HashedPassword;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.LoginTime;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.MemberPrincipal;
import com.acsredux.core.members.values.MemberStatus;
import com.acsredux.core.members.values.RegistrationDate;
import com.acsredux.core.members.values.SessionID;
import com.acsredux.core.members.values.VerificationToken;
import com.acsredux.core.members.values.ZipCode;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;

public class TestData {

  // So tests can open files both when run from IntelliJ and the makefile.
  public static String projectRoot() {
    String cwd = System.getProperty("user.dir");
    return cwd.contains("acsredux/src/") ? "../../" : ".";
  }

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
    TEST_SECOND_LOGIN_TIME,
    false
  );
  public static final CreateMember TEST_CREATE_MEMBER_CMD = new CreateMember(
    TEST_SUBJECT,
    TEST_FIRST_NAME,
    TEST_LAST_NAME,
    TEST_EMAIL,
    TEST_CLEAR_TEXT_PASSWORD,
    TEST_CLEAR_TEXT_PASSWORD,
    TEST_ZIP_CODE
  );
  public static final CreateMember TEST_ADD_MEMBER2_CMD = new CreateMember(
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
    Duration.ofDays(365),
    "<script data-goatcounter='https://mysite.goatcounter.com/count' async src='//example.com/count.js'></script>"
  );

  public static final ContentID TEST_CONTENT_ID = new ContentID(123L);
  public static final AltText TEST_ALT_TEXT = new AltText("img1");
  public static final Image TEST_IMAGE = new Image(
    new ImageSource("/static/img1.png"),
    TEST_ALT_TEXT
  );
  public static final PhotoID TEST_PHOTO_ID = new PhotoID(1000L);
  public static final Title TEST_SECTION_TITLE = new Title("A Test Section Title");
  public static final Section TEST_SECTION = new Section(
    TEST_SECTION_TITLE,
    List.of(TEST_IMAGE)
  );
  public static final DiaryYear TEST_DIARY_YEAR = new DiaryYear(2022);
  public static final DiaryName TEST_DIARY_NAME = null;
  public static final Title TEST_TITLE = new Title("" + TEST_DIARY_YEAR.val());
  public static final Content TEST_PHOTO_DIARY = new Content(
    TEST_CONTENT_ID,
    TEST_MEMBER_ID,
    TEST_TITLE,
    List.of(TEST_SECTION),
    new PublishedDate(Instant.now())
  );

  public static final CreatePhotoDiary TEST_CREATE_PHOTO_DIARY_COMMAND = new CreatePhotoDiary(
    TEST_SUBJECT,
    TEST_DIARY_YEAR,
    TEST_DIARY_NAME
  );
  public static final PhotoDiaryCreated TEST_PHOTO_DIARY_CREATED = new PhotoDiaryCreated(
    TEST_CREATE_PHOTO_DIARY_COMMAND,
    TEST_CONTENT_ID
  );

  public static final SectionIndex TEST_SECTION_INDEX = new SectionIndex(0);
  public static final FileName TEST_FILE_NAME = new FileName("t.png");
  private static byte[] imgbytes;

  static {
    var imgfn = "cypress/fixtures/10138-80-prospect-business-hours-medium.jpeg";
    try {
      imgbytes = Files.readAllBytes(Paths.get(projectRoot(), imgfn));
    } catch (Exception e) {
      throw new IllegalStateException("can't read " + imgfn, e);
    }
  }

  public static final FileContent TEST_FILE_CONTENT = new FileContent(imgbytes);
  public static final ImageDate TEST_IMAGE_DATE = ImageDate.of("2022-03-19");
  public static final UploadPhoto TEST_UPLOAD_PHOTO_COMMAND = new UploadPhoto(
    TEST_SUBJECT,
    TEST_CONTENT_ID,
    TEST_SECTION_INDEX,
    TEST_FILE_NAME,
    TEST_FILE_CONTENT,
    ImageOrientation.PORTRAIT,
    TEST_IMAGE_DATE
  );
}
