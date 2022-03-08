package com.acsredux.adapter.stub;

import static com.acsredux.core.members.MemberService.hashpw;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.ImageSavedEvent;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.FileContent;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.content.values.PublishedDate;
import com.acsredux.core.content.values.Section;
import com.acsredux.core.content.values.Title;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Stub
  implements
    MemberReader,
    MemberWriter,
    MemberNotifier,
    MemberAdminReader,
    AdminReader,
    ContentReader,
    ContentWriter,
    ImageWriter {

  private final List<Member> members;
  private final Map<VerificationToken, MemberID> tokens;
  private final Map<SessionID, MemberID> sessions;
  private final Map<ContentID, Content> content;
  private String documentRoot = ".";

  public Stub() {
    tokens = new HashMap<>();
    sessions = new HashMap<>();
    members = new ArrayList<>();
    content = new HashMap<>();
    members.add(
      new Member(
        new MemberID(1L),
        new Email("admin@example.com"),
        new FirstName("Bill"),
        new LastName("Russel"),
        new ZipCode("02134"),
        MemberStatus.ACTIVE,
        hashpw(new ClearTextPassword("aabb33DD#".toCharArray())),
        new RegistrationDate(Instant.now()),
        ZoneId.of("US/Eastern"),
        LoginTime.of(Instant.now()),
        LoginTime.of(null),
        true
      )
    );
  }

  public void setDocumentRoot(String x) {
    this.documentRoot = x;
  }

  public Optional<Member> findByID(MemberID x) {
    return members.stream().filter(o -> o.id().equals(x)).findFirst();
  }

  @Override
  public Optional<Member> findByName(FirstName x1, LastName x2) {
    return members
      .stream()
      .filter(o ->
        o.firstName().val().equalsIgnoreCase(x1.val()) &&
        o.lastName().val().equalsIgnoreCase(x2.val())
      )
      .findFirst();
  }

  @Override
  public Optional<Member> findByEmail(Email x) {
    return members
      .stream()
      .filter(o -> o.email().val().equalsIgnoreCase(x.val()))
      .findFirst();
  }

  @Override
  public VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now) {
    var y = new VerificationToken("token" + memberID.val());
    tokens.put(y, memberID);
    return y;
  }

  @Override
  public MemberID addMember(CreateMember cmd, MemberStatus initialStatus, CreatedOn now) {
    long maxID = members
      .stream()
      .map(Member::id)
      .mapToLong(MemberID::val)
      .max()
      .orElse(1);
    MemberID newID = new MemberID(maxID + 1);
    members.add(
      new Member(
        newID,
        cmd.email(),
        cmd.firstName(),
        cmd.lastName(),
        cmd.zipCode(),
        initialStatus,
        hashpw(cmd.password1()),
        new RegistrationDate(Instant.now()),
        ZoneId.of("US/Eastern"),
        LoginTime.of(Instant.now()),
        LoginTime.of(null),
        false
      )
    );
    return newID;
  }

  @Override
  public void memberAdded(MemberAdded event, SiteInfo siteInfo) {
    System.out.println(event);
  }

  @Override
  public SiteInfo getSiteInfo() {
    final URI uri;
    try {
      uri = new URI("http://example.com/suggestionbox.html");
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    return new SiteInfo(
      SiteStatus.ALPHA,
      25, // limitOnAlphaCustomers
      100, // limitOnBetaCustomers
      0, // currentAnnualSubscriptionFeeInCents
      "Kitty Korner",
      "A place where you can share your cat pictures with fellow cat lovers.",
      "30 Main St.",
      "Anywhere",
      "MA",
      "12345",
      "mkbucc@gmail.com",
      uri,
      Duration.ofDays(365)
    );
  }

  @Override
  public MemberID getByToken(VerificationToken x) {
    if (!tokens.containsKey(x)) {
      throw new ValidationException("Expired login token " + x.val());
    }
    return tokens.get(x);
  }

  @Override
  public Member getByID(MemberID x) {
    return members
      .stream()
      .filter(o -> o.id().equals(x))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("no member with ID " + x.val()));
  }

  @Override
  public MemberID updateStatus(MemberID x1, MemberStatus x2) {
    Member y = getByID(x1);
    Member y1 = y.withStatus(x2);
    members.remove(y);
    members.add(y1);
    return x1;
  }

  @Override
  public int countActiveMembers() {
    return members.size();
  }

  @Override
  public void writeSessionID(MemberID x1, SessionID x2) {
    sessions.put(x2, x1);
  }

  @Override
  public void setLastLogin(MemberID x1, LoginTime x2) {
    Member y = getByID(x1);
    Member y1 = new Member(
      y.id(),
      y.email(),
      y.firstName(),
      y.lastName(),
      y.zip(),
      y.status(),
      y.password(),
      y.registeredOn(),
      y.tz(),
      x2,
      y.lastLogin(),
      false
    );
    members.remove(y);
    members.add(y1);
  }

  @Override
  public Optional<Member> findBySessionID(SessionID x) {
    if (!sessions.containsKey(x)) {
      return Optional.empty();
    }
    return findByID(sessions.get(x));
  }

  @Override
  public Content getContent(ContentID x) {
    if (content.containsKey(x)) {
      return content.get(x);
    }
    throw new NotFoundException("No photo diary found with ID " + x.val());
  }

  @Override
  public List<Content> findContentByMemberID(MemberID x) {
    return content
      .values()
      .stream()
      .filter(o -> o.author().equals(x))
      .collect(Collectors.toList());
  }

  @Override
  public ContentID createContent(CreatePhotoDiary x) {
    long maxArticleID = content
      .keySet()
      .stream()
      .mapToLong(ContentID::val)
      .max()
      .orElse(0);
    ContentID contentID = new ContentID(maxArticleID + 1);

    var ys = x
      .subject()
      .getPrincipals()
      .stream()
      .map(ACSMemberPrincipal.class::cast)
      .toList();
    if (ys.isEmpty()) {
      throw new IllegalStateException("no member principal");
    }
    MemberID memberID = ys.get(0).mid();

    content.put(
      contentID,
      new Content(
        contentID,
        memberID,
        x.title(),
        getMonthSections(),
        new PublishedDate(Instant.now())
      )
    );
    return contentID;
  }

  private List<Section> getMonthSections() {
    return Stream
      .of(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
      )
      .map(o -> new Section(new Title(o), Collections.emptyList()))
      .collect(Collectors.toList());
  }

  // ---------------------------------------------------------------------------
  //
  //       I M A G E    W R I T E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public ImageSavedEvent save(Instant instant, MemberID x1, FileContent x2, FileName x3) {
    String fmt = "%s/static/img/%d/%s";
    String fn = String.format(fmt, this.documentRoot, x1.val(), x3.val());
    try {
      Path path = Paths.get(fn);
      makeParentDir(path);
      Files.write(path, x2.val());
    } catch (Exception e) {
      throw new IllegalStateException("can't write to fn", e);
    }
    return new ImageSavedEvent();
  }

  private void makeParentDir(Path path) {
    if (path.toFile().getParent() != null) {
      File parentDir = new File(path.toFile().getParent());
      if (!parentDir.exists()) {
        parentDir.mkdirs();
      }
    }
  }

  @Override
  public FileContent resize(FileContent x1, int pixelHeight) {
    return x1;
  }
}
