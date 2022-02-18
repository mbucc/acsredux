package com.acsredux.adapter.web.common;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.core.base.Command;
import com.acsredux.core.members.commands.AddMember;
import com.acsredux.core.members.commands.LoginMember;
import org.junit.jupiter.api.Test;

class TestWebUtil {

  @Test
  void testFormToAddMemberCommand() {
    // setup
    FormData x = new FormData();
    x.add("email", TEST_EMAIL.val());
    x.add("firstName", TEST_FIRST_NAME.val());
    x.add("lastName", TEST_LAST_NAME.val());
    x.add("zip", TEST_ZIP_CODE.val());
    x.add("pwd1", new String(TEST_CLEAR_TEXT_PASSWORD.val()));
    x.add("pwd2", new String(TEST_CLEAR_TEXT_PASSWORD.val()));
    x.add("command", FormCommand.CREATE.name());

    // execute
    Command y = WebUtil.form2cmd(TEST_PRINCIPAL, x);

    // verify
    if (y instanceof AddMember y1) {
      assertEquals(TEST_EMAIL, y1.email());
      assertEquals(TEST_FIRST_NAME, y1.firstName());
      assertEquals(TEST_LAST_NAME, y1.lastName());
      assertEquals(TEST_ZIP_CODE, y1.zipCode());
      assertArrayEquals(TEST_CLEAR_TEXT_PASSWORD.val(), y1.password1().val());
      assertArrayEquals(TEST_CLEAR_TEXT_PASSWORD.val(), y1.password2().val());
    } else {
      fail("wrong command returned");
    }
  }

  @Test
  void testFormToLoginCommand() {
    // setup
    FormData x = new FormData();
    x.add("email", TEST_EMAIL.val());
    x.add("pwd", new String(TEST_CLEAR_TEXT_PASSWORD.val()));
    x.add("command", FormCommand.LOGIN.name());

    // execute
    Command y = WebUtil.form2cmd(TEST_PRINCIPAL, x);

    // verify
    if (y instanceof LoginMember y1) {
      assertEquals(TEST_EMAIL, y1.email());
      assertArrayEquals(TEST_CLEAR_TEXT_PASSWORD.val(), y1.password().val());
    } else {
      fail("wrong command returned");
    }
  }
}
