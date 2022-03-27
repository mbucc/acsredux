package com.acsredux.core.content;

import com.acsredux.core.base.Event;
import com.acsredux.core.base.MemberID;
import com.acsredux.core.content.commands.BaseContentCommand;
import com.acsredux.core.content.entities.Content;
import com.acsredux.core.content.values.ContentID;
import java.util.List;

public interface ContentService {
  // Commands
  List<Event> handle(BaseContentCommand x);

  // Queries
  Content getByID(ContentID x);
  List<Content> findByMemberID(MemberID x);
  List<Content> findChildrenOfID(ContentID x);

  // Queries with a default implementation.
  default List<Content> findTopLevelContentByMemberID(MemberID x) {
    return findByMemberID(x).stream().filter(o -> o.refersTo() == null).toList();
  }
}
