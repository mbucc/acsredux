package com.acsredux.lib.testutil;

import static com.acsredux.core.content.values.ContentType.PHOTO;
import static com.acsredux.core.content.values.ContentType.PHOTO_DIARY;
import static com.acsredux.core.members.PasswordUtil.hashpw;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.base.Subject;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.commands.DeleteContent;
import com.acsredux.core.content.commands.UploadPhoto;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.ContentCreated;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.commands.LoginMember;
import com.acsredux.core.members.commands.VerifyEmail;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.values.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TestData {

  // So tests can open files both when run from IntelliJ and the makefile.
  public static String projectRoot() {
    String cwd = System.getProperty("user.dir");
    return cwd.contains("acsredux/src/") ? "../../" : ".";
  }

  // Use a constant date so dates in test HTML are deterministic.
  public static final Instant TEST_NOW = Instant.parse("2022-04-01T10:26:05Z");

  public static final Email TEST_EMAIL = new Email("test@example.com");
  public static final Email TEST_EMAIL2 = new Email("test2@example.com");
  public static final FirstName TEST_FIRST_NAME = new FirstName("小川");
  public static final LastName TEST_LAST_NAME = new LastName("治兵衛");

  public static final FirstName TEST_FIRST_NAME2 = new FirstName("Bill");
  public static final LastName TEST_LAST_NAME2 = new LastName("Walton");

  public static final MemberID TEST_MEMBER_ID = new MemberID(123L);
  public static final MemberID TEST_MEMBER2_ID = new MemberID(345L);
  public static final Principal TEST_PRINCIPAL = new MemberPrincipal(TEST_MEMBER_ID);
  public static final Subject TEST_SUBJECT = new Subject(TEST_MEMBER_ID);

  public static final ClearTextPassword TEST_CLEAR_TEXT_PASSWORD = ClearTextPassword.of(
    "a3cDefg!"
  );
  public static final VerificationToken TEST_VERIFICATION_TOKEN = new VerificationToken(
    "test token"
  );
  public static final ZipCode TEST_ZIP_CODE = new ZipCode("02134");
  public static final ZipCode TEST_ZIP_CODE2 = new ZipCode("44106");
  public static final MemberStatus TEST_MEMBER_STATUS = MemberStatus.ACTIVE;
  public static final HashedPassword TEST_HASHED_PASSWORD = hashpw(
    TEST_CLEAR_TEXT_PASSWORD
  );
  public static final RegistrationDate TEST_REGISTRATION_DATE = new RegistrationDate(
    Instant.ofEpochSecond(1642250459)
  );
  public static final ZoneId TEST_TIME_ZONE = ZoneId.of("US/Eastern");
  public static final LoginTime TEST_LOGIN_TIME = new LoginTime(TEST_NOW);
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
  public static final Member TEST_MEMBER2 = new Member(
    TEST_MEMBER2_ID,
    TEST_EMAIL2,
    TEST_FIRST_NAME2,
    TEST_LAST_NAME2,
    TEST_ZIP_CODE2,
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

  public static final ContentID TEST_DIARY_CONTENT_ID = new ContentID(123L);
  public static final ContentID TEST_PHOTO_CONTENT_ID = new ContentID(124L);
  public static final ContentID TEST_COMMENT_ID = new ContentID(125L);

  public static final CreatedOn TEST_CREATED_ON = new CreatedOn(TEST_NOW);
  public static final ImageDate TEST_PHOTO_TAKEN_ON = new ImageDate(
    TEST_NOW.minus(15, ChronoUnit.DAYS)
  );

  public static final AltText TEST_ALT_TEXT = new AltText("img1");
  public static final Image TEST_IMAGE = new Image(
    new ImageSource("/static/img1.png"),
    ImageOrientation.PORTRAIT,
    TEST_PHOTO_TAKEN_ON,
    TEST_ALT_TEXT
  );
  public static final PhotoID TEST_PHOTO_ID = new PhotoID(1000L);
  public static final DiaryYear TEST_DIARY_YEAR = new DiaryYear(2022);
  public static final DiaryName TEST_DIARY_NAME = null;
  public static final Title TEST_TITLE = new Title("" + TEST_DIARY_YEAR.val());

  public static final PublishedDate TEST_PUBLISHED_ON = new PublishedDate(TEST_NOW);

  public static final Content TEST_PHOTO_DIARY_CONTENT = new Content(
    TEST_DIARY_CONTENT_ID,
    null,
    TEST_MEMBER_ID,
    TEST_TITLE,
    TEST_CREATED_ON,
    new FromDateTime(TEST_CREATED_ON.val()),
    null,
    PHOTO_DIARY,
    BlobType.MARKDOWN,
    new BlobBytes("Hello World".getBytes(StandardCharsets.UTF_8))
  );
  public static final FileName TEST_IMAGE_FILE_NAME = new FileName("t.png");
  public static final FileName TEST_IMAGE_STD_FILE_NAME = new FileName("t.std.png");
  public static final FileName TEST_IMAGE_ORIG_FILE_NAME = new FileName("t.orig.png");
  public static final Content TEST_PHOTO_CONTENT = new Content(
    TEST_PHOTO_CONTENT_ID,
    TEST_DIARY_CONTENT_ID,
    TEST_MEMBER_ID,
    TEST_TITLE,
    TEST_CREATED_ON,
    new FromDateTime(TEST_PHOTO_TAKEN_ON.val()),
    new UptoDateTime(TEST_PHOTO_TAKEN_ON.val()),
    PHOTO,
    BlobType.IMAGE_LANDSCAPE_HREF,
    new BlobBytes(TEST_IMAGE_STD_FILE_NAME.val().getBytes(StandardCharsets.UTF_8))
  );
  public static final Content TEST_COMMENT = new Content(
    TEST_COMMENT_ID,
    TEST_DIARY_CONTENT_ID,
    TEST_MEMBER_ID,
    TEST_TITLE,
    TEST_CREATED_ON,
    new FromDateTime(TEST_CREATED_ON.val()),
    null,
    PHOTO_DIARY,
    BlobType.MARKDOWN,
    new BlobBytes("Great!".getBytes(StandardCharsets.UTF_8))
  );

  public static final CreatePhotoDiary TEST_CREATE_PHOTO_DIARY_COMMAND = new CreatePhotoDiary(
    TEST_SUBJECT,
    TEST_DIARY_YEAR,
    TEST_DIARY_NAME
  );
  public static final NewContent TEST_NEW_CONTENT_DIARY = new NewContent(
    null,
    TEST_SUBJECT.memberID(),
    TEST_PHOTO_DIARY_CONTENT.title(),
    TEST_PHOTO_DIARY_CONTENT.createdOn(),
    new FromDateTime(TEST_PHOTO_DIARY_CONTENT.createdOn().val()),
    null,
    TEST_PHOTO_DIARY_CONTENT.contentType(),
    TEST_PHOTO_DIARY_CONTENT.blobType(),
    TEST_PHOTO_DIARY_CONTENT.body()
  );

  public static final Content newContentDiaryWithID(ContentID id) {
    return new Content(
      id,
      TEST_NEW_CONTENT_DIARY.refersTo(),
      TEST_NEW_CONTENT_DIARY.createdBy(),
      TEST_NEW_CONTENT_DIARY.title(),
      TEST_NEW_CONTENT_DIARY.createdOn(),
      TEST_NEW_CONTENT_DIARY.from(),
      TEST_NEW_CONTENT_DIARY.upto(),
      TEST_NEW_CONTENT_DIARY.contentType(),
      TEST_NEW_CONTENT_DIARY.blobType(),
      TEST_NEW_CONTENT_DIARY.body()
    );
  }

  public static final ContentCreated TEST_PHOTO_DIARY_CREATED = new ContentCreated(
    TEST_NEW_CONTENT_DIARY,
    TEST_DIARY_CONTENT_ID
  );

  public static final SectionIndex TEST_SECTION_INDEX = new SectionIndex(0);

  private static byte[] imgbytes;

  static {
    var imgfn = "cypress/fixtures/10138-80-prospect-business-hours-medium.jpeg";
    try {
      imgbytes = Files.readAllBytes(Paths.get(projectRoot(), imgfn));
    } catch (Exception e) {
      throw new IllegalStateException("can't read " + imgfn, e);
    }
  }

  public static final BlobBytes TEST_IMAGE_BLOB = new BlobBytes(imgbytes);
  public static final UploadPhoto TEST_UPLOAD_PHOTO_COMMAND = new UploadPhoto(
    TEST_SUBJECT,
    TEST_DIARY_CONTENT_ID,
    TEST_IMAGE_FILE_NAME,
    TEST_IMAGE_BLOB,
    ImageOrientation.PORTRAIT,
    TEST_PHOTO_TAKEN_ON,
    TEST_TIME_ZONE
  );
  public static final DeleteContent TEST_DELETE_CONTENT_COMMAND = new DeleteContent(
    TEST_SUBJECT,
    TEST_PHOTO_CONTENT_ID
  );
}
