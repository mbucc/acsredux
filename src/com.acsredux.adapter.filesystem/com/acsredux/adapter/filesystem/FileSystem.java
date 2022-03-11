package com.acsredux.adapter.filesystem;

import static com.acsredux.core.members.MemberService.hashpw;

import com.acsredux.core.admin.ports.AdminReader;
import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.base.Command;
import com.acsredux.core.base.NotFoundException;
import com.acsredux.core.base.ValidationException;
import com.acsredux.core.content.commands.CreatePhotoDiary;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.events.ImageSavedEvent;
import com.acsredux.core.content.ports.ContentReader;
import com.acsredux.core.content.ports.ContentWriter;
import com.acsredux.core.content.ports.ImageWriter;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.content.values.DocumentRoot;
import com.acsredux.core.content.values.FileContent;
import com.acsredux.core.content.values.FileName;
import com.acsredux.core.content.values.Image;
import com.acsredux.core.content.values.ImageSource;
import com.acsredux.core.content.values.PublishedDate;
import com.acsredux.core.content.values.Section;
import com.acsredux.core.content.values.SectionIndex;
import com.acsredux.core.content.values.Title;
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
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public final class FileSystem
  implements
    MemberReader,
    MemberWriter,
    MemberAdminReader,
    AdminReader,
    ContentReader,
    ContentWriter,
    ImageWriter {

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
      throw new IllegalStateException("can't load members", e);
    }
  }

  private void writeMembersToJSON() {
    try (FileWriter writer = new FileWriter(MEMBERS_JSON_FILE)) {
      List<MemberDTO> ys = this.members.stream().map(MemberDTO::new).toList();
      gson.toJson(ys, writer);
    } catch (Exception e) {
      throw new IllegalStateException("can't write members", e);
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
      throw new IllegalStateException("can't load tokens", e);
    }
  }

  private void writeTokensToJSON() {
    try (FileWriter writer = new FileWriter(TOKENS_JSON_FILE)) {
      List<TokenDTO> ys = this.tokens.entrySet().stream().map(TokenDTO::new).toList();
      gson.toJson(ys, writer);
    } catch (Exception e) {
      throw new IllegalStateException("can't write tokens", e);
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
      throw new IllegalStateException("can't load sessions", e);
    }
  }

  private void writeSessionsToJSON() {
    try (FileWriter writer = new FileWriter(SESSION_JSON_FILE)) {
      List<SessionDTO> ys =
        this.sessions.entrySet().stream().map(SessionDTO::new).toList();
      gson.toJson(ys, writer);
    } catch (Exception e) {
      throw new IllegalStateException("can't write sessions", e);
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
      throw new IllegalStateException("can't load content", e);
    }
  }

  private void writeContentToJSON() {
    try (FileWriter writer = new FileWriter(CONTENT_JSON_FILE)) {
      List<ContentDTO> ys = this.content.values().stream().map(ContentDTO::new).toList();
      gson.toJson(ys, writer);
    } catch (Exception e) {
      throw new IllegalStateException("can't write content", e);
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
      throw new IllegalStateException("can't load tokens", e);
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
      .filter(o -> o.author().equals(x))
      .collect(Collectors.toList());
  }

  // ---------------------------------------------------------------------------
  //
  //       C O N T E N T    W R I T E R
  //
  // ---------------------------------------------------------------------------

  @Override
  public synchronized ContentID createContent(CreatePhotoDiary x) {
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
        cmd2mid(x),
        x.title(),
        getMonthSections(),
        new PublishedDate(Instant.now())
      )
    );
    writeContentToJSON();
    return contentID;
  }

  // TODO: Create our own Subject interface as getACSMemberPrincipal().
  private MemberID cmd2mid(Command x) {
    var ys = x
      .subject()
      .getPrincipals()
      .stream()
      .map(ACSMemberPrincipal.class::cast)
      .toList();
    if (ys.isEmpty()) {
      throw new IllegalStateException("no member principal");
    }
    return ys.get(0).mid();
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

  public synchronized void addPhotoToDiary(ContentID x1, SectionIndex x2, Image x3) {
    content.put(x1, getByID(x1).with(x2, x3));
    writeContentToJSON();
  }

  // ---------------------------------------------------------------------------
  //
  //       I M A G E    W R I T E R
  //
  // ---------------------------------------------------------------------------

  // TODO: This code is duplicated between Stub and FileSystem.
  @Override
  public ImageSavedEvent save(Instant instant, MemberID x1, FileContent x2, FileName x3) {
    try {
      ImageSource imgsrc = src(x1, x3);
      Path path = Paths.get(this.documentRoot.val(), imgsrc.val());
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
