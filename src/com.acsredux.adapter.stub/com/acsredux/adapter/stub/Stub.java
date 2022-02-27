package com.acsredux.adapter.stub;

import static com.acsredux.core.members.MemberService.hashpw;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.admin.values.SiteStatus;
import com.acsredux.core.articles.commands.CreateArticleCommand;
import com.acsredux.core.articles.ports.ArticleReader;
import com.acsredux.core.articles.ports.ArticleWriter;
import com.acsredux.core.articles.values.Article;
import com.acsredux.core.articles.values.ArticleID;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Stub
  implements
    MemberReader,
    MemberWriter,
    MemberNotifier,
    MemberAdminReader,
    AdminReader,
    ArticleReader,
    ArticleWriter {

  private static final Stub INSTANCE = new Stub();

  private final List<Member> members;
  private final Map<VerificationToken, MemberID> tokens;
  private final Map<SessionID, MemberID> sessions;
  private final Map<ArticleID, Article> articles;

  private Stub() {
    tokens = new HashMap<>();
    sessions = new HashMap<>();
    members = new ArrayList<>();
    articles = new HashMap<>();
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

  // From java.util.ServiceLoader documentation (Java17):
  //        If the service provider declares a provider method, then the service
  //        loader invokes that method to obtain an instance of the service
  //        provider. A provider method is a public static method named "provider"
  //        with no formal parameters and a return type that is assignable to
  //        the service's interface or class.
  public static Stub provider() {
    return Stub.INSTANCE;
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
  public MemberID addMember(AddMember cmd, MemberStatus initialStatus, CreatedOn now) {
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
  public Article getArticle(ArticleID x) {
    return articles.get(x);
  }

  @Override
  public List<Article> findArticlesByMemberID(MemberID x) {
    return articles
      .values()
      .stream()
      .filter(o -> o.author().equals(x))
      .collect(Collectors.toList());
  }

  @Override
  public ArticleID createArticle(CreateArticleCommand x) {
    long maxArticleID = articles
      .keySet()
      .stream()
      .mapToLong(ArticleID::val)
      .max()
      .orElse(0);
    ArticleID aid = new ArticleID(maxArticleID + 1);
    articles.put(aid, x.article());
    return aid;
  }
}
