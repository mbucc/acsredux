package com.acsredux.core.content.events;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.commands.SaveNoteText;
import com.acsredux.core.members.values.UpdatedOn;

public record ContentTextUpdated(
  SaveNoteText cmd,
  MemberID updatedBy,
  UpdatedOn updatedOn
)
  implements Event {}
