package com.acsredux.adapter.dbstub;

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

public class Stub implements Reader, Writer, Notifier {

  private Map<Email, User> emailToUser;
  private Map<UserID, UserDashboard> idToUserDashboard;

  public Stub() {
    List<User> users = List.of(
      new User(
        new UserID(1L),
        new Email("test@example.com"),
        new FirstName("Bill"),
        new LastName("Russel"),
        new GrowingZone("5A")
      )
    );
    emailToUser =
      users.stream().collect(Collectors.toMap(User::email, Function.identity()));
    Function<User, UserDashboard> dashboard = o -> new UserDashboard(o);
    idToUserDashboard = users.stream().collect(Collectors.toMap(User::id, dashboard));
  }

  @Override
  public Optional<User> findByEmail(Email x) {
    return Optional.ofNullable(emailToUser.get(x));
  }

  @Override
  public Optional<UserDashboard> findUserDashboard(FindUserDashboard x) {
    return Optional.ofNullable(idToUserDashboard.get(x.userID()));
  }

  @Override
  public VerificationToken addAddUserToken(UserID userID, CreatedOn now) {
    return new VerificationToken("abc123");
  }

  @Override
  public UserID addUser(AddUser cmd, CreatedOn now) {
    return new UserID(2L);
  }

  @Override
  public void userAdded(UserAdded event) {
    return;
  }
}
