package com.acsredux.adapter.mailgun;

import static com.acsredux.lib.testutil.TestData.TEST_SITEINFO;

import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.Notifier;
import com.acsredux.core.members.values.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ManualTestOfMailGunSend {

  Notifier notifier = new MailgunNotifier();

  @Test
  void testNotifyOfMemberAddedSunny() {
    // setup
    AddMember cmd = new AddMember(
      new FirstName("Bill"),
      new LastName("Russell"),
      new Email("mkbucc@gmail.com"),
      new ClearTextPassword("aBcd3f123!"),
      new ClearTextPassword("aBcd3f123!")
    );
    MemberAdded event = new MemberAdded(
      cmd,
      new CreatedOn(Instant.now()),
      new VerificationToken("def345"),
      new MemberID(2L)
    );

    // execute
    notifier.memberAdded(event, TEST_SITEINFO);
    // verify: go check my inbox.
  }
}
