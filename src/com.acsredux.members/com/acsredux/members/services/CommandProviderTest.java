package com.acsredux.members.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.members.CommandService;
import com.acsredux.members.ports.Reader;
import com.acsredux.members.queries.FindMemberDashboard;
import com.acsredux.members.values.MemberDashboard;
import com.acsredux.members.values.MemberID;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandProviderTest {

  private CommandService service;
  private Reader reader;

  @BeforeEach
  void setup() {
    this.reader = mock(Reader.class);
    this.service = new CommandProvider(this.reader, null, null, null);
  }

  @Test
  void testSunnyPath() {
    // setup
    MemberID memberID = new MemberID(1L);
    FindMemberDashboard qry = new FindMemberDashboard(memberID);
    given(reader.findMemberDashboard(qry)).willReturn(Optional.empty());

    // execute
    Optional<MemberDashboard> y = assertDoesNotThrow(() -> service.handle(qry));

    // verify
    then(reader).should().findMemberDashboard(qry);
  }
}
