package com.acsredux.adapter.filesystem;

import static com.acsredux.core.members.MemberService.hashpw;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.*;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.values.*;
import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.ports.MemberAdminReader;
import com.acsredux.core.members.ports.MemberReader;
import com.acsredux.core.members.ports.MemberWriter;
import com.acsredux.core.members.values.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FileSystem
  implements
    MemberReader,
    MemberWriter,
    MemberAdminReader,
    AdminReader,
    ContentReader,
    ContentWriter {

  private List<Member> members = new ArrayList<>();
  private Map<VerificationToken, MemberID> tokens = new HashMap<>();
  private Map<SessionID, MemberID> sessions = new HashMap<>();
  private Map<ContentID, Content> content = new HashMap<>();
  private SiteInfo siteInfo;

  private DocumentRoot documentRoot = new DocumentRoot(".");

  // JSON
  private static final Type MEMBER_TYPE = new TypeToken<List<MemberDTO>>() {}.getType();
  private static final Type TOKEN_TYPE = new TypeToken<List<TokenDTO>>() {}.getType();
  private static final Type SESSION_TYPE = new TypeToken<List<SessionDTO>>() {}.getType();
  private static final Type CONTENT_TYPE = new TypeToken<List<ContentDTO>>() {}.getType();

  public static final String MEMBERS_JSON_FILE = "members.json";
  public static final String TOKENS_JSON_FILE = "tokens.json";
  public static final String SESSION_JSON_FILE = "sessions.json";
  public static final String CONTENT_JSON_FILE = "content.json";
  public static final String SITEINFO_JSON_FILE = "siteinfo.json";

  private final Gson gson;

  public FileSystem() {
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    gson = builder.create();

    readMembersFromJSON();
    readTokensFromJSON();
    readSessionsFromJSON();
    readContentFromJSON();
    readSiteInfoFromJSON();
  }

  private void readMembersFromJSON() {
    File fp = new File(MEMBERS_JSON_FILE);
    if (!fp.exists()) {
      return;
    }
    try (Reader reader = Files.newBufferedReader(fp.toPath())) {
      List<MemberDTO> ys = gson.fromJson(reader, MEMBER_TYPE);
      this.members = ys.stream().map(MemberDTO::asMember).collect(Collectors.toList());
    } catch (Exception e) {
      throw new IllegalStateException("can't read '" + MEMBERS_JSON_FILE + "'", e);
    }
  }

  static void rename(String from, String to) throws IOException {
    Path x = Paths.get(from);
    Files.move(x, x.resolveSibling(to), StandardCopyOption.REPLACE_EXISTING);
  }

  private void writeMembersToJSON() {
    String tmp = MEMBERS_JSON_FILE + ".tmp";
    try (FileWriter writer = new FileWriter(tmp)) {
      List<MemberDTO> ys = this.members.stream().map(MemberDTO::new).toList();
      gson.toJson(ys, writer);
      rename(tmp, MEMBERS_JSON_FILE);
    } catch (Exception e) {
      throw new IllegalStateException("can't write '" + MEMBERS_JSON_FILE + "'", e);
    }
  }

  private void readTokensFromJSON() {
    File fp = new File(TOKENS_JSON_FILE);
    if (!fp.exists()) {
      return;
    }
    try (Reader reader = Files.newBufferedReader(fp.toPath())) {
      List<TokenDTO> ys = gson.fromJson(reader, TOKEN_TYPE);
      this.tokens = new HashMap<>();
      for (TokenDTO y : ys) {
        this.tokens.put(y.getVerificationToken(), y.getMemberID());
      }
    } catch (Exception e) {
      throw new IllegalStateException("can't read '" + TOKENS_JSON_FILE + "'", e);
    }
  }

  private void writeTokensToJSON() {
    String tmp = TOKENS_JSON_FILE + ".tmp";
    try (FileWriter writer = new FileWriter(tmp)) {
      List<TokenDTO> ys = this.tokens.entrySet().stream().map(TokenDTO::new).toList();
      gson.toJson(ys, writer);
      rename(tmp, TOKENS_JSON_FILE);
    } catch (Exception e) {
      throw new IllegalStateException("can't write '" + TOKENS_JSON_FILE + "'", e);
    }
  }

  private void readSessionsFromJSON() {
    File fp = new File(SESSION_JSON_FILE);
    if (!fp.exists()) {
      return;
    }
    try (Reader reader = Files.newBufferedReader(fp.toPath())) {
      List<SessionDTO> ys = gson.fromJson(reader, SESSION_TYPE);
      this.sessions = new HashMap<>();
      for (SessionDTO y : ys) {
        this.sessions.put(y.getSessionID(), y.getMemberID());
      }
    } catch (Exception e) {
      throw new IllegalStateException("can't read '" + SESSION_JSON_FILE + "'", e);
    }
  }

  private void writeSessionsToJSON() {
    String tmp = SESSION_JSON_FILE + ".tmp";
    try (FileWriter writer = new FileWriter(SESSION_JSON_FILE)) {
      List<SessionDTO> ys =
        this.sessions.entrySet().stream().map(SessionDTO::new).toList();
      gson.toJson(ys, writer);
      rename(tmp, SESSION_JSON_FILE);
    } catch (Exception e) {
      throw new IllegalStateException("can't write '" + SESSION_JSON_FILE + "'", e);
    }
  }

  private void readContentFromJSON() {
    File fp = new File(CONTENT_JSON_FILE);
    if (!fp.exists()) {
      return;
    }
    try (Reader reader = Files.newBufferedReader(fp.toPath())) {
      List<ContentDTO> ys = gson.fromJson(reader, CONTENT_TYPE);
      this.content = new HashMap<>(ys.size());
      for (ContentDTO y : ys) {
        Content y1 = y.asContent();
        this.content.put(y1.id(), y1);
      }
    } catch (Exception e) {
      throw new IllegalStateException("can't read '" + CONTENT_JSON_FILE + "'", e);
    }
  }

  private void writeContentToJSON() {
    String tmp = CONTENT_JSON_FILE + ".tmp";
    try (FileWriter writer = new FileWriter(tmp)) {
      List<ContentDTO> ys = this.content.values().stream().map(ContentDTO::new).toList();
      gson.toJson(ys, writer);
      rename(tmp, CONTENT_JSON_FILE);
    } catch (Exception e) {
      throw new IllegalStateException("can't write '" + CONTENT_JSON_FILE + "'", e);
    }
  }

  private void readSiteInfoFromJSON() {
    File fp = new File(SITEINFO_JSON_FILE);
    if (!fp.exists()) {
      throw new IllegalStateException(SITEINFO_JSON_FILE + " not found");
    }
    try (Reader reader = Files.newBufferedReader(fp.toPath())) {
      SiteInfoDTO y = gson.fromJson(reader, SiteInfoDTO.class);
      this.siteInfo = y.asSiteInfo();
    } catch (Exception e) {
      throw new IllegalStateException("can't read '" + SITEINFO_JSON_FILE + "'", e);
    }
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
  public synchronized VerificationToken addAddMemberToken(
    MemberID memberID,
    CreatedOn now
  ) {
    var y = new VerificationToken("token" + memberID.val());
    tokens.put(y, memberID);
    writeTokensToJSON();
    return y;
  }

  @Override
  public synchronized MemberID addMember(
    CreateMember cmd,
    MemberStatus initialStatus,
    CreatedOn now
  ) {
    long maxID = members
      .stream()
      .map(Member::id)
      .mapToLong(MemberID::val)
      .max()
      .orElse(0);
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
    writeMembersToJSON();
    return newID;
  }

  @Override
  public SiteInfo getSiteInfo() {
    return this.siteInfo;
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
  public synchronized MemberID updateStatus(MemberID x1, MemberStatus x2) {
    Member y = getByID(x1);
    Member y1 = y.withStatus(x2);
    members.remove(y);
    members.add(y1);
    writeMembersToJSON();
    return x1;
  }

  @Override
  public int countActiveMembers() {
    return members.size();
  }

  @Override
  public synchronized void writeSessionID(MemberID x1, SessionID x2) {
    sessions.put(x2, x1);
    writeSessionsToJSON();
  }

  @Override
  public synchronized void setLastLogin(MemberID x1, LoginTime x2) {
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
    writeMembersToJSON();
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
    if (content.containsKey(x)) {
      return content.get(x);
    }
    throw new NotFoundException("No photo diary found with ID " + x.val());
  }

  @Override
  public List<Content> findByMemberID(MemberID x) {
    return content
      .values()
      .stream()
      .filter(o -> o.createdBy().equals(x))
      .collect(Collectors.toList());
  }

  @Override
  public List<Content> findChildrenOfID(ContentID x) {
    return content
      .values()
      .stream()
      .filter(o -> Objects.equals(x, o.refersTo()))
      .toList();
  }

  // ---------------------------------------------------------------------------
  //
  //       C O N T E N T    W R I T E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public synchronized ContentID save(NewContent x) {
    long maxArticleID = content
      .keySet()
      .stream()
      .mapToLong(ContentID::val)
      .max()
      .orElse(0);
    ContentID contentID = new ContentID(maxArticleID + 1);
    content.put(
      contentID,
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
    writeContentToJSON();
    return contentID;
  }

  @Override
  public synchronized void update(Content x) {
    content.put(x.id(), x);
  }

  @Override
  public synchronized void delete(ContentID x) {
    content.remove(x);
  }
}
