package com.acsredux.core.content;

import com.acsredux.core.base.Event;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.values.Content;
import com.acsredux.core.content.values.ContentID;
import com.acsredux.core.members.values.MemberID;
import java.util.List;

public interface ContentService {
  // Queries
  Content getContent(ContentID x);
  List<Content> findArticlesByMemberID(MemberID x);

  // Commands
  List<Event> handle(BaseContentCommand x);
}
