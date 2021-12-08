package com.acsredux.user.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.inOrder;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.acsredux.base.values.UserID;
import com.acsredux.user.QueryHandler;
import com.acsredux.user.ports.Reader;
import com.acsredux.user.queries.FindUserDashboard;
import com.acsredux.user.services.QueryHandlerImpl;
import com.acsredux.user.values.UserDashboard;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryHandlerTest {

  private QueryHandler service;
  private Reader reader;

  @BeforeEach
  void setup() {
    this.reader = mock(Reader.class);
    this.service = new QueryHandlerImpl(this.reader);
  }

  @Test
  void testSunnyPath() {
    // setup
    UserID userID = new UserID(1L);
    FindUserDashboard qry = new FindUserDashboard(userID);
    given(reader.findUserDashboard(qry)).willReturn(Optional.empty());

    // execute
    Optional<UserDashboard> y = assertDoesNotThrow(() -> service.handle(qry));

    // verify
    then(reader).should().findUserDashboard(qry);
  }
}
