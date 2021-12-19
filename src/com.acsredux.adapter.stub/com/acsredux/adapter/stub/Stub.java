package com.acsredux.adapter.stub;

import com.acsredux.members.commands.AddMember;
import com.acsredux.members.entities.Member;
import com.acsredux.members.events.MemberAdded;
import com.acsredux.members.ports.Notifier;
import com.acsredux.members.ports.Reader;
import com.acsredux.members.ports.Writer;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.CreatedOn;
import com.acsredux.members.values.Email;
import com.acsredux.members.values.FirstName;
import com.acsredux.members.values.GrowingZone;
import com.acsredux.members.values.LastName;
import com.acsredux.members.values.MemberDashboard;
import com.acsredux.members.values.MemberID;
import com.acsredux.members.values.VerificationToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class Stub implements Reader, Writer, Notifier {

  private static final Stub INSTANCE = new Stub();

  private List<Member> members;

  private Stub() {
    members = new ArrayList<>();
    members.add(
      new Member(
        new MemberID(1L),
        new Email("test@example.com"),
        new FirstName("Bill"),
        new LastName("Russel"),
        new GrowingZone("5A")
      )
    );
  }

  // From java.util.ServiceLoader documentation (v17):
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
  public Optional<MemberDashboard> findMemberDashboard(FindMemberDashboard x) {
    Function<Member, MemberDashboard> toDashboard = o -> new MemberDashboard(o);
    System.out.println();
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
    members.add(
      new Member(newID, cmd.email(), cmd.firstName(), cmd.lastName(), cmd.zone())
    );
    return newID;
  }

  @Override
  public void memberAdded(MemberAdded event) {
    System.out.println(event);
    return;
  }
}
