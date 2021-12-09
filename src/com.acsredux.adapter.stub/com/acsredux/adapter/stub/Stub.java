package com.acsredux.adapter.stub;

import com.acsredux.base.entities.User;
import com.acsredux.base.values.CreatedOn;
import com.acsredux.base.values.Email;
import com.acsredux.base.values.FirstName;
import com.acsredux.base.values.GrowingZone;
import com.acsredux.base.values.LastName;
import com.acsredux.base.values.UserID;
import com.acsredux.base.values.VerificationToken;
import com.acsredux.user.commands.AddUser;
import com.acsredux.user.events.UserAdded;
import com.acsredux.user.ports.Notifier;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.ports.Writer;
import com.acsredux.user.queries.FindUserDashboard;
import com.acsredux.user.values.UserDashboard;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Stub implements Reader, Writer, Notifier {

  private static final Stub INSTANCE = new Stub();

  private List<User> users;

  private Stub() {
    users =
      List.of(
        new User(
          new UserID(1L),
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
  public Optional<User> findByEmail(Email x) {
    return users
      .stream()
      .filter(o -> o.email().val().equalsIgnoreCase(x.val()))
      .findFirst();
  }

  @Override
  public Optional<UserDashboard> findUserDashboard(FindUserDashboard x) {
    Function<User, UserDashboard> toDashboard = o -> new UserDashboard(o);
    return users.stream().filter(x.userID()::equals).findFirst().map(toDashboard);
  }

  @Override
  public VerificationToken addAddUserToken(UserID userID, CreatedOn now) {
    return new VerificationToken("abc123");
  }

  @Override
  public UserID addUser(AddUser cmd, CreatedOn now) {
    Long maxID = users.stream().map(User::id).mapToLong(UserID::val).max().orElse(0);
    UserID newID = new UserID(maxID + 1);
    users.add(new User(newID, cmd.email(), cmd.firstName(), cmd.lastName(), cmd.zone()));
    return newID;
  }

  @Override
  public void userAdded(UserAdded event) {
    System.out.println(event);
    return;
  }
}
