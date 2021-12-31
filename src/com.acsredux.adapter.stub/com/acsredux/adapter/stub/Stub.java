package com.acsredux.adapter.stub;

import com.acsredux.core.admin.values.SiteInfo;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.entities.Member;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.AdminReader;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.ports.Reader;
import com.acsredux.core.members.ports.Writer;
import com.acsredux.core.members.queries.FindDashboard;
import com.acsredux.core.members.values.CreatedOn;
import com.acsredux.core.members.values.Email;
import com.acsredux.core.members.values.FirstName;
import com.acsredux.core.members.values.LastName;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import com.acsredux.core.members.values.VerificationToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class Stub implements Reader, Writer, Notifier, AdminReader {

  private static final Stub INSTANCE = new Stub();

  private List<Member> members;

  private Stub() {
    members = new ArrayList<>();
    members.add(
      new Member(
        new MemberID(1L),
        new Email("admin@example.com"),
        new FirstName("Bill"),
        new LastName("Russel")
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

  @Override
  public Optional<Member> findByEmail(Email x) {
    return members
      .stream()
      .filter(o -> o.email().val().equalsIgnoreCase(x.val()))
      .findFirst();
  }

  @Override
  public Optional<MemberDashboard> findMemberDashboard(FindDashboard x) {
    Function<Member, MemberDashboard> toDashboard = o -> new MemberDashboard(o);
    return members
      .stream()
      .filter(o -> o.id().equals(x.memberID()))
      .findFirst()
      .map(toDashboard);
  }

  @Override
  public VerificationToken addAddMemberToken(MemberID memberID, CreatedOn now) {
    return new VerificationToken("abc123");
  }

  @Override
  public MemberID addMember(AddMember cmd, CreatedOn now) {
    Long maxID = members
      .stream()
      .map(Member::id)
      .mapToLong(MemberID::val)
      .max()
      .orElse(1);
    MemberID newID = new MemberID(maxID + 1);
    members.add(new Member(newID, cmd.email(), cmd.firstName(), cmd.lastName()));
    return newID;
  }

  @Override
  public void memberAdded(MemberAdded event, SiteInfo siteInfo) {
    System.out.println(event);
    return;
  }

  @Override
  public SiteInfo getSiteInfo() {
    return new SiteInfo(
      25, // limitOnAlphaCustomers
      100, // limitOnBetaCustomers
      0, // currentAnnualSubscriptionFeeInCents
      "Kitty Korner",
      "<p>A place where you can share your cat pictures with fellow cat lovers.</p>",
      "30 Main St.",
      "Anywhere",
      "MA",
      "12345",
      "ceo@acxredux.com"
    );
  }
}
