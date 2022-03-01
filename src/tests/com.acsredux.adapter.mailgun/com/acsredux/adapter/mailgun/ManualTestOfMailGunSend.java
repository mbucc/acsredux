package com.acsredux.adapter.mailgun;

import static com.acsredux.lib.testutil.TestData.*;

import com.acsredux.core.members.commands.CreateMember;
import com.acsredux.core.members.events.MemberAdded;
import com.acsredux.core.members.ports.MemberNotifier;
import com.acsredux.core.members.values.*;
import java.time.Instant;

class ManualTestOfMailGunSend {

  final MemberNotifier notifier = new MailgunNotifier();

  void testNotifyOfMemberAddedSunny() {
    // setup
    CreateMember cmd = new CreateMember(
      TEST_SUBJECT,
      new FirstName("Bill"),
      new LastName("Russell"),
      new Email("mkbucc@gmail.com"),
      ClearTextPassword.of("aBcd3f123!"),
      ClearTextPassword.of("aBcd3f123!"),
      new ZipCode("12345")
    );
    MemberAdded event = new MemberAdded(
      cmd,
      new CreatedOn(Instant.now()),
      new VerificationToken("def345"),
      new MemberID(2L)
    );

    // execute
    notifier.memberAdded(event, TEST_SITE_INFO);
    // verify: go check my inbox.
  }
}
