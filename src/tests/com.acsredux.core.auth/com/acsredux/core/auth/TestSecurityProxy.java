package com.acsredux.core.auth;

import static com.acsredux.lib.testutil.TestData.TEST_SUBJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acsredux.core.base.Command;
import com.acsredux.core.base.Event;
import com.acsredux.core.members.MemberService;
import com.acsredux.lib.testutil.MockMemberService;
import com.acsredux.lib.testutil.TestData;
import java.lang.reflect.Method;
import java.util.List;
import javax.security.auth.Subject;
import org.junit.jupiter.api.Test;

public class TestSecurityProxy {

  interface TestService {
    List<Event> handle(Command x);
  }

  static class TestProvider implements TestService {

    @Override
    public List<Event> handle(Command x) {
      return null;
    }
  }

  static class MockCommand implements Command {

    Subject subject;

    public MockCommand(Subject subject) {
      this.subject = subject;
    }

    @Override
    public Subject subject() {
      return this.subject;
    }
  }

  @Test
  void testDumpMethodSignatureWithNullArg() throws NoSuchMethodException {
    // setup
    Method m = TestProvider.class.getMethod("handle", Command.class);
    Object[] args = new Object[] { null };

    // execute
    String y = SecurityProxy.dump(m, args);

    // verify
    assertEquals(this.getClass().getName() + "$TestProvider.handle(null x1)", y);
  }

  @Test
  void testDumpMethodSignatureWithNonNullArg() throws NoSuchMethodException {
    // setup
    Method m = TestProvider.class.getMethod("handle", Command.class);
    Object[] args = new Object[] { new MockCommand(new Subject()) };

    // execute
    String y = SecurityProxy.dump(m, args);

    // verify
    assertEquals(this.getClass().getName() + "$TestProvider.handle(MockCommand x1)", y);
  }

  static class MockSecurityPolicy implements SecurityPolicy {

    @Override
    public boolean isAllowed(Method m, Object[] args) {
      return false;
    }

    @Override
    public Subject getSubject(Object[] args) {
      return TEST_SUBJECT;
    }
  }

  @Test
  void testMethodIsNotAllowed() {
    // setup
    MemberService x = SecurityProxy.of(new MockMemberService(), new MockSecurityPolicy());

    // execute
    SecurityPolicyException y = assertThrows(
      SecurityPolicyException.class,
      () -> x.handle(TestData.TEST_ADD_MEMBER_CMD)
    );

    // verify
    assertEquals(
      "Subject: Principal: MemberPrincipal[mid=MemberID[val=123]] " +
      "denied access to " +
      "com.acsredux.core.members.MemberService.handle(CreateMember x1)",
      y.getMessage()
    );
  }
}
