package com.acsredux.adapter.web;

import static com.acsredux.lib.testutil.TestData.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.acsredux.core.base.Command;
import com.acsredux.core.members.commands.AddMember;
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
    x.add("pwd1", TEST_PASSWORD.val());
    x.add("pwd2", TEST_PASSWORD.val());
    x.add("command", FormCommand.CREATE_MEMBER.name());

    // execute
    Command y = WebUtil.form2cmd(x);

    // verify
    if (y instanceof AddMember y1) {
      assertEquals(TEST_EMAIL, y1.email());
      assertEquals(TEST_FIRST_NAME, y1.firstName());
      assertEquals(TEST_LAST_NAME, y1.lastName());
      assertEquals(TEST_ZIP_CODE, y1.zipCode());
      assertEquals(TEST_PASSWORD, y1.password1());
      assertEquals(TEST_PASSWORD, y1.password2());
    } else {
      fail("wrong command returned");
    }
  }

  @Test
  void testParseFormDataWorksWithEmptyString() {
    FormData y = assertDoesNotThrow(() -> WebUtil.parseFormData(""));
  }
}
