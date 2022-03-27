package com.acsredux.adapter.stub;

import static com.acsredux.core.members.MemberService.hashpw;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageReader;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public final class Stub
  implements
    MemberReader,
    MemberWriter,
    MemberNotifier,
    MemberAdminReader,
    AdminReader,
    ContentReader,
    ContentWriter,
    ImageWriter,
    ImageReader {

  private final List<Member> members = new ArrayList<>();
  private final Map<VerificationToken, MemberID> tokens = new HashMap<>();
  private final Map<SessionID, MemberID> sessions = new HashMap<>();
  private final List<Content> content = new ArrayList<>();
  private DocumentRoot documentRoot = new DocumentRoot(".");

  public Stub() {
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
    this.documentRoot = new DocumentRoot(x);
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
      Duration.ofDays(365),
      ""
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

  // ---------------------------------------------------------------------------
  //
  //       C O N T E N T    R E A D E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public Content getByID(ContentID x) {
    return content
      .stream()
      .filter(o -> o.id().equals(x))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No photo diary found with ID " + x.val())
      );
  }

  @Override
  public List<Content> findByMemberID(MemberID x) {
    return content.stream().filter(o -> Objects.equals(x, o.createdBy())).toList();
  }

  @Override
  public List<Content> findChildrenOfID(ContentID x) {
    return content.stream().filter(o -> Objects.equals(x, o.refersTo())).toList();
  }

  // ---------------------------------------------------------------------------
  //
  //       C O N T E N T    W R I T E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public ContentID save(NewContent x) {
    long max = content
      .stream()
      .map(Content::id)
      .mapToLong(ContentID::val)
      .max()
      .orElse(0);
    ContentID contentID = new ContentID(max + 1);
    content.add(
      new Content(
        contentID,
        x.refersTo(),
        x.createdBy(),
        x.title(),
        x.createdOn(),
        x.from(),
        x.upto(),
        x.contentType(),
        x.blobType(),
        x.content()
      )
    );
    return contentID;
  }

  @Override
  public void update(Content y) {
    throw new UnsupportedOperationException("implement");
  }

  // ---------------------------------------------------------------------------
  //
  //       I M A G E    W R I T E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public void save(MemberID x1, BlobBytes x2, FileName x3) {
    try {
      ImageSource imgsrc = src(x1, x3);
      Path path = Paths.get(this.documentRoot.val(), imgsrc.val());
      makeParentDir(path);
      Files.write(path, x2.val());
    } catch (Exception e) {
      throw new IllegalStateException("can't write to fn", e);
    }
  }

  private void makeParentDir(Path path) {
    if (path.toFile().getParent() != null) {
      File parentDir = new File(path.toFile().getParent());
      if (!parentDir.exists()) {
        parentDir.mkdirs();
      }
    }
  }

  // ---------------------------------------------------------------------------
  //
  //       I M A G E    R E A D E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public byte[] readPlaceholderImage() {
    Path fn = Paths.get(this.documentRoot.val(), "/static/img/placeholder.png");
    try {
      return Files.readAllBytes(fn);
    } catch (IOException e) {
      throw new IllegalStateException("can't read " + fn, e);
    }
  }
}
