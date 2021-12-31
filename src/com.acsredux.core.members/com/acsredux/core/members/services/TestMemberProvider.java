package com.acsredux.core.members.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.acsredux.core.members.MemberService;
import com.acsredux.core.members.ports.Reader;
import com.acsredux.core.members.queries.FindDashboard;
import com.acsredux.core.members.values.MemberDashboard;
import com.acsredux.core.members.values.MemberID;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestMemberProvider {

  private MemberService service;
  private Reader reader;

  @BeforeEach
  void setup() {
    this.reader = mock(Reader.class);
    this.service = new MemberProvider(this.reader, null, null, null, null);
  }

  @Test
  void testSunnyPath() {
    // setup
    MemberID memberID = new MemberID(1L);
    FindDashboard qry = new FindDashboard(memberID);
    given(reader.findMemberDashboard(qry)).willReturn(Optional.empty());

    // execute
    Optional<MemberDashboard> y = assertDoesNotThrow(() -> service.handle(qry));

    // verify
    then(reader).should().findMemberDashboard(qry);
  }
}
